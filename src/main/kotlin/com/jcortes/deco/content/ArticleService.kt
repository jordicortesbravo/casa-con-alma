package com.jcortes.deco.content

import com.jcortes.deco.client.BedrockTextClient
import com.jcortes.deco.content.categorizer.SiteCategorizer
import com.jcortes.deco.content.model.Article
import com.jcortes.deco.content.model.ArticleSearchRequest
import com.jcortes.deco.util.Pageable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.services.bedrockruntime.model.ThrottlingException

@Service
class ArticleService(
    private val bedrockTextClient: BedrockTextClient,
    private val articleRepository: ArticleRepository,
    private val imageService: ImageService
) {

    @Lazy
    @Autowired
    private lateinit var self: ArticleService

    fun get(id: Long): Article {
        return articleRepository.get(id) ?: throw NoSuchElementException("Article $id not found")
    }

    fun generateFromFile() {
        ClassPathResource("article_themes.csv").file.useLines { lines ->
            lines.forEach { line ->
                val (title, subtitle) = line.split(";")
                val article = Article().apply {
                    this.title = title
                    this.subtitle = subtitle
                }
                self.save(article)
            }
        }
    }

    fun enrich() {
        val articles = articleRepository.iterate().asSequence().toList()
        articles.parallelStream().forEach { article ->
            try{
                var siteCategories = SiteCategorizer.categorize(article.title!!)
                siteCategories = siteCategories.takeUnless { it.isNullOrEmpty() } ?: SiteCategorizer.categorize(article.content!!)
                article.siteCategories = siteCategories
                article.content = bedrockTextClient.invokeTextModel(userPrompt = article.content, systemPrompt = DESCRIPTION_PROMPT)
                article.keywords = bedrockTextClient.invokeTextModel(userPrompt = article.content, systemPrompt = KEYWORDS_PROMPT) { extractKeywords(it) }
                article.embedding = bedrockTextClient.invokeEmbeddingModel(userPrompt = article.content!!)
                self.save(article)
            } catch (te: ThrottlingException) {
              Thread.sleep(20_000)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Transactional
    fun save(article: Article) {
        articleRepository.save(article)
    }

    fun search(query: String?, siteCategories: List<String>, pageable: Pageable): List<Article> {
        val embedding = query?.takeUnless { it.isBlank() }?.let { bedrockTextClient.invokeEmbeddingModel(userPrompt = it) }
        val request = ArticleSearchRequest(embedding = embedding, siteCategories = siteCategories, pageSize = pageable.pageSize, pageNumber = pageable.pageNumber)
        return articleRepository.search(request)
    }

    private fun extractKeywords(text: String?): List<String> {
        return text?.split(",")?.map { it.trim() } ?: emptyList()
    }

    companion object {
        private const val DESCRIPTION_PROMPT =
            "Eres un sistema encargado de resumir artículos de un blog de decoración e interiorismo. Tu principal cometido es hacer un resumen claro y conciso de no más de 2048 caracteres para poder generar un embedding para realizar búsquedas semánticas de contenido. Ignorarás por completo los tags html que se pasen en la entrada y te centrarás solamente en el texto. Es importante que detalles qué tipo de artículos se detallan en los artículos para poder relacionar los artículos con imágenes en procesos posteriores."

        private const val KEYWORDS_PROMPT =
            "Describe keywords del artículo. Da prioridad a la zona de la casa (si aplica) y el resto que describan estilo decorativo, mobiliario, materiales y texturas. Solo retorna un listado separado por comas y todo en minúsculas."

        private const val CLASSIFIER_PROMPT =
            "Clasifica el artículo en una o varias categorías. Solo retorna un listado separado por comas y todo en minúsculas."
    }
}