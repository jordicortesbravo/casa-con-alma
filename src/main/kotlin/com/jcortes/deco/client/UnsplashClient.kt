package com.jcortes.deco.client

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jcortes.deco.content.model.Image
import com.jcortes.deco.util.Page
import com.jcortes.deco.util.Pageable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.math.abs

class UnsplashClient(
    private val downloadBaseDir: File,
    private val baseUrl: String,
    private val apiKey: String
) {

    private val client = HttpClient.newBuilder().build()
    private val objectMapper = jacksonObjectMapper()
    private val downloadedIds = ConcurrentSkipListSet<String>()

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    fun downloadByTags(tags: List<String>, startPageable: Pageable? = null, imagesProcessor: ((images: Page<Image>) -> Unit)? = null) {
        tags.forEach { tag ->
            val dir = File(downloadBaseDir, tag)
            dir.mkdirs()
            log.info("Downloading images for tag: $tag")
            var pageable = startPageable ?: Pageable(1, 30)
            try {
                do {
                    val photos = searchPhotos(tag, pageable)
                    downloadPhotos(dir, photos)
                    downloadedIds.addAll(photos.items.map { it.sourceId })
                    imagesProcessor?.invoke(photos)
                    pageable = pageable.next()
                    log.info("Downloaded page ${pageable.pageNumber} for tag $tag")
                } while (photos.hasNextPage())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun downloadPhotos(baseDir: File, photos: Page<Image>) {
        val client = HttpClient.newBuilder().build()
        val filteredPhotos = photos.items.filter { it.sourceId !in downloadedIds }
        filteredPhotos.forEach { photo ->
            val request = HttpRequest.newBuilder()
                .uri(URI(photo.sourceUrl))
                .GET()
                .build()
            client.send(request, HttpResponse.BodyHandlers.ofInputStream()).body().use { inputStream ->
                val fileName = "${photo.sourceId.substringAfter("::")}.jpeg"
                val dir = File(baseDir, (abs(photo.sourceId.hashCode() % 50)).toString().padStart(2, '0'))
                dir.mkdirs()
                val file = File(dir, fileName)
                photo.internalUri = file.toURI().toString()
                Files.copy(inputStream, file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING)
            }
        }
    }

    private fun searchPhotos(query: String, page: Pageable): Page<Image> {
        val request = requestBuilder("search/photos?query=${URLEncoder.encode(query, Charsets.UTF_8)}&page=${page.pageNumber}&per_page=${page.pageSize}")
            .GET()
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val json = objectMapper.readTree(response.body())
        val photos = json["results"].map { photo ->
            Image().apply {
                sourceId = "unsplash::${photo["id"].asText()}"
                keywords = photo["tags"]?.map { it["title"].asText() }?.plus(query) ?: listOf(query)
                description = photo["description"]?.textValue()
                sourceUrl = photo["urls"]["regular"].asText()
                author = photo["user"]["name"].asText()
            }
        }
        return Page.of(page, json["total"].asInt(), photos)
    }

    private fun requestBuilder(url: String): HttpRequest.Builder {
        return HttpRequest.newBuilder()
            .header("Content-Type", "application/json")
            .header("Authorization", "Client-ID $apiKey")
            .uri(URI.create("$baseUrl/$url"))
    }
}