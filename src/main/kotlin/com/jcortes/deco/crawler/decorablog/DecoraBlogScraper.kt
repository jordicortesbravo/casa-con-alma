package com.jcortes.deco.crawler.decorablog

import com.jcortes.deco.content.model.ScrapedDocument
import com.jcortes.deco.crawler.GenericScraper
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.net.URI

class DecoraBlogScraper : GenericScraper() {

    override fun scrap(source: String, url: String, html: String): ScrapedDocument? {
        return Jsoup.parse(html).selectFirst(".site-content")?.let { document ->
            val title = document.select("h1.entry-title").firstOrNull()?.text()
            val subtitle = document.select(".panel-principal p").firstOrNull()?.text()
            val relatedLinks = document.select("a[href]").mapNotNull { it.attr("abs:href") }.filter { it.isNotBlank() }
            val content = scrapContent(document)
            return ScrapedDocument().apply {
                this.url = URI(url)
                this.title = requireNotNull(title) { "Title not found" }
                this.subtitle = subtitle
                this.content = requireNotNull(content) { "Content not found" }
                this.relatedLinks = relatedLinks.map { URI(it) }
            }
        }
    }

    override fun scrapContent(element: Element): String? {
        val articleContent = element.selectFirst(".entry-content") ?: return null

        val contentBuilder = StringBuilder()
        traverse(articleContent, contentBuilder)
        val content = contentBuilder.toString()
        return content
    }

    override fun traverse(node: Element, contentBuilder: StringBuilder) {
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