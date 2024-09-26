package com.jcortes.deco.crawler

import com.jcortes.deco.content.categorizer.ProductCategorizer
import com.jcortes.deco.content.categorizer.SiteCategorizer
import com.jcortes.deco.content.model.ScrapedDocument
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.URI
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

interface Scraper {
    fun scrap(source: String, url: String, html: String): ScrapedDocument?
}

open class GenericScraper : Scraper {

    override fun scrap(source:String, url: String, html: String): ScrapedDocument? {
        val document = Jsoup.parse(html)
        val sourceId = scrapSourceId(document, url)
        val title = document.select("h1").firstOrNull()?.text()
        val subtitle = document.select("h2").firstOrNull()?.text()
        val updateInstant = scrapUpdateInstant(document, url)
        val relatedLinks = document.select("a[href]").mapNotNull { it.attr("abs:href") }.filter { it.isNotBlank() }
        val content = scrapContent(document)
        val productCategories = ProductCategorizer.categorize("$title $subtitle")
        val siteCategories = productCategories?.let { SiteCategorizer.categorize("$title $subtitle") }
        return ScrapedDocument().apply {
            this.sourceId = sourceId?.let{ "$source::$sourceId" }
            this.url = URI(url)
            this.title = title
            this.updateInstant = updateInstant
            this.subtitle = subtitle
            this.siteCategories = siteCategories
            this.productCategories = productCategories
            this.content = content
            this.relatedLinks = relatedLinks.map { URI(it) }
        }
    }

    protected open fun scrapSourceId(document: Document, url: String): String? {
        return "generic::${url}"
    }

    protected open fun scrapUpdateInstant(document: Document, url: String): Instant? {
        return try {
            LocalDate.parse(document.select("meta[name='DC.date.issued']").attr("content").toString()).atStartOfDay().toInstant(ZoneOffset.UTC)
        } catch (e: Exception) {
            null
        }
    }

    protected open fun scrapContent(element: Element): String? {
        val articleContent = element.selectFirst("div.article-content") ?: return null

        val contentBuilder = StringBuilder()
        traverse(articleContent, contentBuilder)
        val content = contentBuilder.toString()
        return content

    }

    protected open fun traverse(node: Element, contentBuilder:StringBuilder) {
        node.children().forEach { element ->
            when (element.tagName()) {
                "h2", "h3" -> contentBuilder.append("<h3>").append(element.text()).append("</h3>\n")
                "p" -> contentBuilder.append("<p>").append(element.text()).append("</p>\n")
                "img" -> {
                    val imgUrl = element.attr("src")
                    contentBuilder.append("<img src=$imgUrl/>").append("\n")
                }
                else -> traverse(element, contentBuilder)
            }
        }
    }
}