package com.jcortes.deco.web.api

import com.jcortes.deco.content.ScrapedDocumentService
import com.jcortes.deco.tools.crawler.Crawler
import com.jcortes.deco.tools.crawler.decoesfera.DecoEsferaCrawler
import com.jcortes.deco.tools.crawler.decorablog.DecoraBlogCrawler
import com.jcortes.deco.tools.crawler.elmueble.ElMuebleCrawler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.ConcurrentSkipListSet

@RestController
@RequestMapping("/crawl")
class CrawlerController(
    private val elMuebleCrawler: ElMuebleCrawler,
    private val decoraBlogCrawler: DecoraBlogCrawler,
    private val decoEsferaCrawler: DecoEsferaCrawler,
    private val scrapedDocumentService: ScrapedDocumentService
) {

    @GetMapping("/el-mueble")
    fun crawlElMueble() {
        crawl(elMuebleCrawler, "https://www.elmueble.com")
    }

    @GetMapping("decora-blog")
    fun crawlDecoraBlog() {
        crawl(decoraBlogCrawler, "https://www.decorablog.com/")
    }

    @GetMapping("deco-esfera")
    fun crawlDecoEsfera() {
        crawl(decoEsferaCrawler, "https://decoracion.trendencias.com/")
    }

    private fun crawl(crawler: Crawler, startUrl: String) {
        val processedSourceIds = ConcurrentSkipListSet(scrapedDocumentService.processedSourceIds().toMutableList())

        crawler.run(startUrl) { doc ->
            if (doc.sourceId != null) {
                synchronized(processedSourceIds) {
                    if (!processedSourceIds.contains(doc.sourceId)) {
                        processedSourceIds.add(doc.sourceId)
                        scrapedDocumentService.save(doc)
                    }
                }
            }
        }
    }
}