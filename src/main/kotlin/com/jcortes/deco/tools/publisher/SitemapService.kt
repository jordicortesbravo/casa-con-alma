package com.jcortes.deco.tools.publisher

import com.idealista.yaencontre.io.storage.Storage
import com.jcortes.deco.content.ArticleRepository
import com.jcortes.deco.content.model.Article
import com.jcortes.deco.content.model.ArticleStatus
import com.jcortes.deco.content.model.DecorTag
import com.jcortes.deco.content.model.SiteCategory
import com.jcortes.deco.util.url.UrlBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDate

@Service
class SitemapService(
    private val articleRepository: ArticleRepository,
    private val urlBuilder: UrlBuilder,
    private val staticResourcesStorage: Storage
) {

    @Value("\${app.content-base-url}")
    private lateinit var siteUrl: String

    private val log = LoggerFactory.getLogger(this::class.java)

    fun publishSitemap() {
        log.info("Generating sitemap")
        val articles = articleRepository.iterate().asSequence().filter{ it.status == ArticleStatus.READY_TO_PUBLISH }.toList()

        val mainSitemap = generateMainSitemap()
        val urlSitemap = generateUrlSitemap(articles)
        val imageSitemap = generateImageSitemap(articles)

        generateIndexSitemap(mainSitemap, urlSitemap, imageSitemap)
        log.info("Sitemap generated")
    }

    private fun generateMainSitemap(): String {
        val sitemapBuilder = StringBuilder()
        sitemapBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        sitemapBuilder.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap-image/1.1\">")

        // Generar URL de la página de inicio
        sitemapBuilder.append("<url>")
        sitemapBuilder.append("<loc>${urlBuilder.contentUrl("home")}</loc>")
        sitemapBuilder.append("<lastmod>${LocalDate.now()}</lastmod>")
        sitemapBuilder.append("<changefreq>daily</changefreq>")
        sitemapBuilder.append("<priority>1.0</priority>")
        sitemapBuilder.append("</url>")

        // Generar URLs de las categorías
        for (category in SiteCategory.entries) {
            sitemapBuilder.append("<url>")
            sitemapBuilder.append("<loc>${urlBuilder.contentUrl(category.seoUrl)}</loc>")
            sitemapBuilder.append("<lastmod>${LocalDate.now()}</lastmod>")
            sitemapBuilder.append("<changefreq>weekly</changefreq>")
            sitemapBuilder.append("<priority>0.8</priority>")
            sitemapBuilder.append("</url>")
        }

        // Generar URLs de los tags
        for (tag in DecorTag.entries) {
            sitemapBuilder.append("<url>")
            sitemapBuilder.append("<loc>${urlBuilder.contentUrl(tag.seoUrl)}</loc>")
            sitemapBuilder.append("<lastmod>${LocalDate.now()}</lastmod>")
            sitemapBuilder.append("<changefreq>weekly</changefreq>")
            sitemapBuilder.append("<priority>0.5</priority>")
            sitemapBuilder.append("</url>")
        }

        sitemapBuilder.append("</urlset>")
        return sitemapBuilder.toString()
    }

    private fun generateUrlSitemap(articles: List<Article>): String {
        val sitemapBuilder = StringBuilder()
        sitemapBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        sitemapBuilder.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap-image/1.1\">")

        for (article in articles) {
            sitemapBuilder.append("<url>")
            sitemapBuilder.append("<loc>${urlBuilder.contentUrl(article.seoUrl)}</loc>")
            sitemapBuilder.append("<lastmod>${LocalDate.now()}</lastmod>")
            sitemapBuilder.append("<changefreq>monthly</changefreq>")
            sitemapBuilder.append("<priority>0.8</priority>")
            sitemapBuilder.append("</url>")
        }

        sitemapBuilder.append("</urlset>")
        return sitemapBuilder.toString()
    }

    private fun generateImageSitemap(articles: List<Article>): String {
        val sitemapBuilder = StringBuilder()
        sitemapBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        sitemapBuilder.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap-image/1.1\">")

        for(article in articles) {
            val images = article.images ?: emptyList()
            for (image in images) {
                sitemapBuilder.append("<url>")
                sitemapBuilder.append("<loc>${urlBuilder.contentUrl(article.seoUrl)}</loc>")
                sitemapBuilder.append("<image:image>")
                sitemapBuilder.append("<image:loc>${urlBuilder.imageUrl(image.seoUrl)}</image:loc>")
                sitemapBuilder.append("<image:caption>${image.caption}</image:caption>")
                sitemapBuilder.append("</image:image>")
                sitemapBuilder.append("</url>")
            }
        }
        sitemapBuilder.append("</urlset>")
        return sitemapBuilder.toString()
    }

    private fun generateIndexSitemap(mainSitemap: String, urlSitemap: String, imageSitemap: String) {
        val indexSitemapBuilder = StringBuilder()
        indexSitemapBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        indexSitemapBuilder.append("<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap-image/1.0\">")

        // Añadir sitemap principal
        indexSitemapBuilder.append("<sitemap>")
        indexSitemapBuilder.append("<loc>${siteUrl}sitemap-main.xml</loc>")
        indexSitemapBuilder.append("<lastmod>${LocalDate.now()}</lastmod>")
        indexSitemapBuilder.append("</sitemap>")

        // Añadir sitemap de URLs
        indexSitemapBuilder.append("<sitemap>")
        indexSitemapBuilder.append("<loc>${siteUrl}sitemap-urls.xml</loc>")
        indexSitemapBuilder.append("<lastmod>${LocalDate.now()}</lastmod>")
        indexSitemapBuilder.append("</sitemap>")

        // Añadir sitemap de imágenes
        indexSitemapBuilder.append("<sitemap>")
        indexSitemapBuilder.append("<loc>${siteUrl}sitemap-images.xml</loc>")
        indexSitemapBuilder.append("<lastmod>${LocalDate.now()}</lastmod>")
        indexSitemapBuilder.append("</sitemap>")

        indexSitemapBuilder.append("</sitemapindex>")

        // Guardar los sitemaps en archivos
        publish("sitemap-main.xml", mainSitemap) // Guarda el sitemap principal
        publish("sitemap-urls.xml", urlSitemap)
        publish("sitemap-images.xml", imageSitemap)
        publish("sitemap-index.xml", indexSitemapBuilder.toString())
    }

    private fun publish(fileName: String, content: String) {
        staticResourcesStorage.put("sitemaps/$fileName", content.byteInputStream(), MediaType.APPLICATION_XML_VALUE)
    }
}
