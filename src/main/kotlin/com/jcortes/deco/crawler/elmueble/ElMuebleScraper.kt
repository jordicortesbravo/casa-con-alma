package com.jcortes.deco.crawler.elmueble

import com.jcortes.deco.crawler.GenericScraper
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element


class ElMuebleScraper : GenericScraper() {

    companion object {
        val REMOVABLE_IMAGES_PATTERN = """<img src=data:image/svg[^>]*>""".toRegex(RegexOption.IGNORE_CASE)
    }

    override fun scrapSourceId(document: Document, url: String): String? {
        return document.select("meta[property='articleid']").attr("content").takeUnless { it.isBlank() }
    }

    override fun scrapContent(element: Element): String? {
        var content = super.scrapContent(element)
        content = content?.replace("\n", "")
            ?.replace(REMOVABLE_IMAGES_PATTERN, "")
            ?.replace(".jpg/", ".jpg")

        return content
    }
}