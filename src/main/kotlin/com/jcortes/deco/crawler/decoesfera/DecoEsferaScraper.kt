package com.jcortes.deco.crawler.decoesfera

import com.jcortes.deco.crawler.GenericScraper
import org.jsoup.nodes.Document
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class DecoEsferaScraper : GenericScraper() {

    override fun scrapSourceId(document: Document, url: String): String? {
        val scripts = document.select("script")

        for (script in scripts) {
            val scriptData = script.data()
            if (scriptData.contains("window.dataLayer")) {
                val regex = """"content_id":\s*(\d+)""".toRegex()
                val matchResult = regex.find(scriptData)
                if (matchResult != null) {
                    return matchResult.groupValues[1]
                }
            }
        }
        return null
    }

    override fun scrapUpdateInstant(document: Document, url: String): Instant? {
        return try {
            LocalDate.parse(document.select("meta[name='DC.date']").attr("content").toString()).atStartOfDay().toInstant(ZoneOffset.UTC)
        } catch (e: Exception) {
            null
        }
    }
}