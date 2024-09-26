package com.jcortes.deco.app

import com.jcortes.deco.crawler.CrawlerConfig
import com.jcortes.deco.crawler.decoesfera.DecoEsferaCrawler
import com.jcortes.deco.crawler.decoesfera.DecoEsferaScraper
import com.jcortes.deco.crawler.decorablog.DecoraBlogCrawler
import com.jcortes.deco.crawler.decorablog.DecoraBlogScraper
import com.jcortes.deco.crawler.elmueble.ElMuebleCrawler
import com.jcortes.deco.crawler.elmueble.ElMuebleScraper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.regex.Pattern

@Configuration
class CrawlerConfig {

    @Bean
    fun elMuebleCrawler(): ElMuebleCrawler {
        val crawler = ElMuebleCrawler()
        crawler.setup(
            CrawlerConfig().apply {
                nThreads = 8
                maxDepth = 3
                acceptedUrlPattern = Pattern.compile("https://www.elmueble.com/.*")
                scraper = ElMuebleScraper()
                source = "el-mueble"
            }
        )
        return crawler
    }

    @Bean
    fun decoEsferaCrawler(): DecoEsferaCrawler {
        val crawler = DecoEsferaCrawler()
        crawler.setup(
            CrawlerConfig().apply {
                nThreads = 8
                maxDepth = 3
                acceptedUrlPattern = Pattern.compile("https://decoracion.trendencias.com/.*")
                scraper = DecoEsferaScraper()
                source = "deco-esfera"
            }
        )
        return crawler
    }

    @Bean
    fun decoraBlogCrawler(): DecoraBlogCrawler {
        val crawler = DecoraBlogCrawler()
        crawler.setup(
            CrawlerConfig().apply {
                nThreads = 8
                maxDepth = 3
                acceptedUrlPattern = Pattern.compile("https://www.decorablog.com/.*")
                scraper = DecoraBlogScraper()
                source = "decora-blog"
            }
        )
        return crawler
    }
}