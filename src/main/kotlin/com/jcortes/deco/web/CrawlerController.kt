package com.jcortes.deco.web

import com.jcortes.deco.crawler.decoesfera.DecoEsferaCrawlerRunner
import com.jcortes.deco.crawler.decorablog.DecoraBlogCrawlerRunner
import com.jcortes.deco.crawler.elmueble.ElMuebleCrawlerRunner
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${app.base-path}/crawl")
class CrawlerController(
    private val elMuebleCrawlerRunner: ElMuebleCrawlerRunner,
    private val decoraBlogCrawlerRunner: DecoraBlogCrawlerRunner,
    private val cecoEsferaCrawlerRunner: DecoEsferaCrawlerRunner
) {

    @GetMapping("/el-mueble")
    fun crawlElMueble() {
        elMuebleCrawlerRunner.run()
    }

    @GetMapping("decora-blog")
    fun crawlDecoraBlog() {
        decoraBlogCrawlerRunner.run()
    }

    @GetMapping("deco-esfera")
    fun crawlDecoEsfera() {
        cecoEsferaCrawlerRunner.run()
    }
}