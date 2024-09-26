package com.jcortes.deco.web

import com.jcortes.deco.content.ScrapedDocumentService
import com.jcortes.deco.crawler.decoesfera.DecoEsferaCrawler
import com.jcortes.deco.crawler.decorablog.DecoraBlogCrawler
import com.jcortes.deco.crawler.elmueble.ElMuebleCrawler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${app.base-path}/crawl")
class CrawlerController(
    private val elMuebleCrawler: ElMuebleCrawler,
    private val decoraBlogCrawler: DecoraBlogCrawler,
    private val decoEsferaCrawler: DecoEsferaCrawler,
    private val scrapedDocumentService: ScrapedDocumentService
) {

    @GetMapping("/el-mueble")
    fun crawlElMueble() {
        val startUrl = "https://www.elmueble.com"
        val processedSourceIds = scrapedDocumentService.processedSourceIds().map { it.substringAfter("::") }.toMutableList()

        elMuebleCrawler.run(startUrl) { doc ->
            if (doc.sourceId != null) {
                scrapedDocumentService.save(doc)
            }
        }
    }

    @GetMapping("decora-blog")
    fun crawlDecoraBlog() {
        val startUrl = "https://www.decorablog.com/"
        val processedSourceIds = scrapedDocumentService.processedSourceIds().map { it.substringAfter("::") }.toMutableList()

        decoraBlogCrawler.run(startUrl) { doc ->
            if (doc.sourceId != null) {
                scrapedDocumentService.save(doc)
            }
        }
    }

    @GetMapping("deco-esfera")
    fun crawlDecoEsfera() {
        val startUrl = "https://decoracion.trendencias.com/"
        val processedSourceIds = scrapedDocumentService.processedSourceIds().map { it.substringAfter("::") }.toMutableList()

        decoEsferaCrawler.run(startUrl) { doc ->
            if (doc.sourceId != null) {
                scrapedDocumentService.save(doc)
            }
        }
    }
}