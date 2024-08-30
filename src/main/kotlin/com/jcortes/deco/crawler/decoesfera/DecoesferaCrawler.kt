package com.jcortes.deco.crawler.decoesfera

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jcortes.deco.crawler.CrawlerConfig
import com.jcortes.deco.crawler.GenericCrawler
import com.jcortes.deco.web.templates.DetailTemplate
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import java.io.File
import java.util.regex.Pattern

fun main() {
    runApplication<DecoEsferaCrawlerRunner> {
        webApplicationType = WebApplicationType.NONE
        setAdditionalProfiles("config")
    }
}

@Component
class DecoEsferaCrawler: GenericCrawler()

@SpringBootApplication
class DecoEsferaCrawlerRunner(
    private val crawler: DecoEsferaCrawler
) : CommandLineRunner {

    private val objectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())
    private val detailTemplate = DetailTemplate()

    override fun run(vararg args: String?) {

        val format = "html"

        val baseFolder = File("/Users/jcortes/workspace/tmp/deco-crawler/deco-esfera/")
        baseFolder.deleteRecursively()
        baseFolder.mkdirs()
        File("/Users/jcortes/workspace/crawler/src/main/resources/web/static").copyRecursively(File("/Users/jcortes/workspace/tmp/deco-crawler/deco-esfera/static"))

        crawler.setup(
            CrawlerConfig().apply {
                nThreads = 8
                maxDepth = 3
                acceptedUrlPattern = Pattern.compile("https://decoracion.trendencias.com/.*")
                scraper = DecoEsferaScraper()
            }
        )

        crawler.run("https://decoracion.trendencias.com/") { doc ->
            if (doc.sourceId != null) {
                when (format) {
                    "json" -> File("/Users/jcortes/workspace/tmp/deco-crawler/deco-esfera/${doc.sourceId}.json").writeText(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(doc))
                    "html" -> File("/Users/jcortes/workspace/tmp/deco-crawler/deco-esfera/${doc.sourceId}.html").writeText(detailTemplate.print(doc))
                }
            }
        }
    }
}