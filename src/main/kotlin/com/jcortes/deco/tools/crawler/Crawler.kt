package com.jcortes.deco.tools.crawler

import com.jcortes.deco.content.model.ScrapedDocument
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.util.regex.Pattern

interface Crawler {
    fun run(startUrl: String, dataProcessor: ((scrapedDocument: ScrapedDocument) -> Unit)? = null): Set<String>
}

class CrawlerConfig {
    var nThreads: Int = 1
    var maxDepth: Int = 2
    var acceptedUrlPattern: Pattern = Pattern.compile(".*")
    lateinit var scraper: Scraper
    lateinit var source: String
}

open class GenericCrawler : Crawler {
    private lateinit var crawler: Crawler

    fun setup(config: CrawlerConfig) {
        this.crawler = ThreadPoolCrawler(config)
    }

    override fun run(startUrl: String, dataProcessor: ((scrapedDocument: ScrapedDocument) -> Unit)?): Set<String> {
        return this.crawler.run(startUrl, dataProcessor)
    }
}

class ThreadPoolCrawler(private val config: CrawlerConfig) : Crawler {

    private val client = HttpClient.newBuilder().build()
    private val executor = Executors.newFixedThreadPool(config.nThreads)
    private val taskCountLatch = TaskCountLatch()
    private val scraper = config.scraper

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun run(startUrl: String, dataProcessor: ((scrapedDocument: ScrapedDocument) -> Unit)?): Set<String> {
        val urls = ConcurrentHashMap.newKeySet<String>()
        crawl(startUrl, 0, urls, dataProcessor)
        shutdown()
        return urls
    }

    private fun crawl(url: String, depth: Int, visitedUrls: MutableSet<String>, dataProcessor: ((scrapedDocument: ScrapedDocument) -> Unit)?) {
        if (depth <= config.maxDepth) {
            fetch(url)?.let { article ->
                dataProcessor?.invoke(article)
                article.relatedLinks?.map { it.toString() }?.forEach { linkedUrl ->
                    if (config.acceptedUrlPattern.matcher(linkedUrl).matches() && !visitedUrls.contains(linkedUrl)) {
                        executor.submit {
                            try {
                                taskCountLatch.increment()
                                crawl(linkedUrl, depth + 1, visitedUrls.plus(linkedUrl), dataProcessor)
                                log.info("Depth: $depth, Link: $linkedUrl")
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                taskCountLatch.decrement()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun fetch(url: String): ScrapedDocument? {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() == 200) {
            return scraper.scrap(config.source, url, response.body())
        }
        return null
    }

    private fun shutdown() {
        taskCountLatch.awaitTermination()
        executor.shutdown()
    }

    private fun <E> MutableSet<E>.plus(element: E): MutableSet<E> {
        this.add(element)
        return this
    }
}

private class TaskCountLatch {
    private val taskCounter = AtomicInteger(0)
    private val lock = Object()

    fun increment() {
        taskCounter.incrementAndGet()
    }

    fun decrement() {
        taskCounter.decrementAndGet()
        synchronized(lock) {
            if (taskCounter.get() == 0) {
                lock.notify() // Notifica al hilo principal si no hay más tareas pendientes
            }
        }
    }

    fun awaitTermination() {
        synchronized(lock) {
            while (taskCounter.get() > 0) {
                try {
                    lock.wait() // Espera hasta que todas las tareas se completen
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    Thread.currentThread().interrupt()
                }
            }
        }
    }
}