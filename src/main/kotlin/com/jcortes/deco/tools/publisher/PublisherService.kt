package com.jcortes.deco.tools.publisher

import com.idealista.yaencontre.io.storage.Storage
import com.jcortes.deco.content.ArticleService
import com.jcortes.deco.content.ImageService
import com.jcortes.deco.content.model.Article
import com.jcortes.deco.content.model.DecorTag
import com.jcortes.deco.content.model.SiteCategory
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


@Service
class PublisherService(
    private val sitemapService: SitemapService,
    private val articleService: ArticleService,
    private val imageService: ImageService,
    private val contentStorage: Storage,
    private val imageStorage: Storage,
    private val staticResourcesStorage: Storage
) {

    val localUrl = "http://localhost:8083"
    val client: HttpClient = HttpClient.newHttpClient()

    private val log = LoggerFactory.getLogger(this::class.java)

    fun publishContent() {
        log.info("Publish process started")
        publishArticles()
        publishCategories()
        publishDecorTagsPages()
        publishHome()
        publishImages()
        publishStaticResources()
        sitemapService.publishSitemap()
        log.info("Publish process ended")
    }

    fun publishArticles() {
        log.info("Publishing articles")
        articleService.listPublishable().forEach { publishArticle(it) }
        log.info("Articles published")
    }

    fun publishArticle(article: Article) {
        article.seoUrl?.let { fetchAndPublishPage(it) }
    }

    fun publishCategories() {
        log.info("Publishing categories")
        SiteCategory.entries.forEach { category ->
            fetchAndPublishPage(category.seoUrl)
        }
        log.info("Categories published")
    }

    fun publishDecorTagsPages() {
        log.info("Publishing decor tags pages")
        DecorTag.entries.forEach { tag ->
            fetchAndPublishPage(tag.seoUrl)
        }
        log.info("Decor tags pages published")
    }

    fun publishImages() {
        log.info("Publishing images")
        imageService.list().forEach { image ->
            URI(image.internalUri!!).toURL().openStream().use { inputStream ->
                imageStorage.put(image.seoUrl!!, inputStream)
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
                file.inputStream().use { staticResourcesStorage.put(path, it) }
            }
        }
        log.info("Static resources published")
    }

    fun publishHome() {
        log.info("Publishing home")
        fetchAndPublishPage("home")
        log.info("Home published")
    }

    private fun fetchAndPublishPage(seoUrl: String) {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$localUrl/$seoUrl"))
            .build()
        client.send(request, HttpResponse.BodyHandlers.ofInputStream()).body().use { inputStream ->
            contentStorage.put(seoUrl, inputStream)
        }
    }
}