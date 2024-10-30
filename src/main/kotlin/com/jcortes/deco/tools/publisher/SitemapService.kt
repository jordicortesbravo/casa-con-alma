package com.jcortes.deco.tools.publisher

import com.jcortes.deco.content.ArticleRepository
import com.jcortes.deco.content.model.Article
import com.jcortes.deco.content.model.ArticleStatus
import com.jcortes.deco.content.model.DecorTag
import com.jcortes.deco.content.model.SiteCategory
import com.jcortes.deco.util.url.UrlBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDate

@Service
class SitemapService(
    private val articleRepository: ArticleRepository,
    private val urlBuilder: UrlBuilder,
) {

    @Value("\${app.content-base-url}")
    private lateinit var siteUrl: String

    fun generateSitemap() {
        val articles = articleRepository.iterate().asSequence().filter{ it.status == ArticleStatus.READY_TO_PUBLISH }.toList()

        val mainSitemap = generateMainSitemap()
        val urlSitemap = generateUrlSitemap(articles)
        val imageSitemap = generateImageSitemap(articles)

        generateIndexSitemap(mainSitemap, urlSitemap, imageSitemap)
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
                sitemapBuilder.append("<image:loc>${urlBuilder.staticUrl(image.seoUrl)}</image:loc>")
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
        saveToFile("sitemap-main.xml", mainSitemap) // Guarda el sitemap principal
        saveToFile("sitemap-urls.xml", urlSitemap)
        saveToFile("sitemap-images.xml", imageSitemap)
        saveToFile("sitemap-index.xml", indexSitemapBuilder.toString())
    }

    private fun saveToFile(fileName: String, content: String) {
        File(fileName).writeText(content)
    }
}
