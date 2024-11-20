package com.jcortes.deco.tools.publisher

import com.jcortes.deco.util.io.storage.Storage
import com.jcortes.deco.util.io.storage.s3.S3Storage
import com.jcortes.deco.content.ArticleService
import com.jcortes.deco.content.ImageService
import com.jcortes.deco.content.model.Article
import com.jcortes.deco.content.model.DecorTag
import com.jcortes.deco.content.model.SiteCategory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files


@Service
class PublisherService(
    private val sitemapService: SitemapService,
    private val articleService: ArticleService,
    private val imageService: ImageService,
    private val contentStorage: Storage,
    private val imageStorage: Storage,
    private val staticResourcesStorage: Storage
) {

    @Value("\${app.content-base-url}")
    private lateinit var contentBaseUrl: String

    @Value("\${app.images-base-url}")
    private lateinit var imagestBaseUrl: String

    @Value("\${app.static-resources-base-url}")
    private lateinit var staticResourcesBaseUrl: String

    val localUrl = "http://localhost:8083"
    val client: HttpClient = HttpClient.newHttpClient()

    private val log = LoggerFactory.getLogger(this::class.java)

    fun publishContent() {
        assertPublish()
        log.info("Publish process started")
//        publishStaticResources()
//        publishImages()
//        publishArticles()
//        publishCategories()
        publishDecorTagsPages()
        publishHome()
//        publishErrorPage()
//        publishRobotsTxt()
        sitemapService.publishSitemap()
        log.info("Publish process ended")
    }

    fun publishArticles() {
        log.info("Publishing articles")
        articleService.listPublishable().parallelStream().forEach { publishArticle(it) }
        log.info("Articles published")
    }

    fun publishArticle(article: Article) {
        article.seoUrl?.let { fetchAndPublishPage(it, "public, max-age=604800, s-maxage=604800") }
    }

    fun publishCategories() {
        log.info("Publishing categories")
        SiteCategory.entries.parallelStream().forEach { category ->
            fetchAndPublishPage(category.seoUrl, "public, max-age=43200, s-maxage=43200")
        }
        log.info("Categories published")
    }

    fun publishDecorTagsPages() {
        log.info("Publishing decor tags pages")
        DecorTag.entries.parallelStream().forEach { tag ->
            fetchAndPublishPage(tag.seoUrl, "public, max-age=43200, s-maxage=43200")
        }
        log.info("Decor tags pages published")
    }

    fun publishImages() {
        log.info("Publishing images")
        imageService.list().parallelStream().forEach { image ->
            URI(image.internalUri!!).toURL().openStream().use { inputStream ->
                imageStorage.put(
                    objectName = "images/${image.seoUrl}",
                    inputStream = inputStream,
                    metadata = mapOf(
                        "Content-Type" to "image/webp",
                        "Cache-Control" to "public, max-age=31536000, s-maxage=31536000"
                    )
                )
            }
            URI(image.internalUri!!.replace(".webp", ".jpeg")).toURL().openStream().use { inputStream ->
                imageStorage.put(
                    objectName = "jpeg/${image.seoUrl!!}",
                    inputStream = inputStream,
                    metadata = mapOf(
                        "Content-Type" to "image/jpeg",
                        "Cache-Control" to "public, max-age=31536000, s-maxage=31536000"
                    )
                )
            }
        }
        log.info("Images published")
    }

    fun publishStaticResources() {
        log.info("Publishing static resources")
        val resource = ClassPathResource("web/static")
        resource.file.walkTopDown().forEach { file ->
            if (file.isFile) {
                val path = file.relativeTo(resource.file).path
                file.inputStream().use {
                    staticResourcesStorage.put(
                        objectName = "static/$path",
                        inputStream = it,
                        metadata = mapOf(
                            "Content-Type" to Files.probeContentType(file.toPath()),
                            "Cache-Control" to "max-age=86400, s-maxage=31536000"
                        )
                    )
                }
            }
        }
        log.info("Static resources published")
    }

    fun publishHome() {
        log.info("Publishing home")
        fetchAndPublishPage("home", "public, max-age=43200, s-maxage=43200")
        log.info("Home published")
    }

    fun publishErrorPage() {
        log.info("Publishing error page")
        fetchAndPublishPage("404", "no-store, no-cache")
        log.info("Error page published")
    }

    fun publishRobotsTxt() {
        log.info("Publishing robots.txt")
        val resource = ClassPathResource("web/static/robots.txt")
        resource.inputStream.use {
            contentStorage.put(
                objectName = "robots.txt",
                inputStream = it,
                metadata = mapOf(
                    "Content-Type" to MediaType.TEXT_PLAIN_VALUE,
                    "Cache-Control" to "public, max-age=86400, s-maxage=86400"
                )
            )
        }
        log.info("Robots.txt published")
    }

    private fun fetchAndPublishPage(seoUrl: String, cacheControl: String) {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$localUrl/$seoUrl"))
            .build()
        client.send(request, HttpResponse.BodyHandlers.ofInputStream()).body().use { inputStream ->
            contentStorage.put(
                objectName = seoUrl,
                inputStream = inputStream,
                metadata = mapOf(
                    "Content-Type" to MediaType.TEXT_HTML_VALUE,
                    "Cache-Control" to cacheControl
                )
            )
        }
    }

    private fun assertPublish() {
        if ((contentStorage is S3Storage && !contentBaseUrl.startsWith("https://www.casaconalma.com"))
            || (imageStorage is S3Storage && !imagestBaseUrl.startsWith("https://www.casaconalma.com/images"))
            || (staticResourcesStorage is S3Storage && !staticResourcesBaseUrl.startsWith("https://www.casaconalma.com/static"))
        ) {
            throw IllegalStateException("Be careful! You are trying to publish to a production environment from a non-production environment")
        }
    }
}