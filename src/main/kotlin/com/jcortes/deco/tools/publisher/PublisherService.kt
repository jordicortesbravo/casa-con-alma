package com.jcortes.deco.tools.publisher

import com.idealista.yaencontre.io.storage.Storage
import com.jcortes.deco.content.ArticleService
import com.jcortes.deco.content.ImageService
import com.jcortes.deco.content.model.Article
import com.jcortes.deco.content.model.DecorTag
import com.jcortes.deco.content.model.SiteCategory
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
) {

    val localUrl = "http://localhost:8083"
    val client: HttpClient = HttpClient.newHttpClient()

    fun publishContent() {
//        publishArticles()

//        publishCategories()
//        publishDecorTagsPages()

        publishImages()

//        publishSitemap()
    }

    fun publishArticles() {
        articleService.listPublishable().forEach { publishArticle(it) }
    }

    fun publishArticle(article: Article) {
        article.seoUrl?.let { fetchAndPublishPage(it) }
    }

    fun publishCategories() {
        SiteCategory.entries.forEach { category ->
            fetchAndPublishPage(category.seoUrl)
        }
    }

    fun publishDecorTagsPages() {
       DecorTag.entries.forEach { tag ->
           fetchAndPublishPage(tag.seoUrl)
        }
    }

    fun publishImages() {
        imageService.list().forEach { image ->
            URI(image.internalUri!!).toURL().openStream().use { inputStream ->
                imageStorage.put(image.seoUrl!!, inputStream)
            }
        }
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