package com.jcortes.deco.content

import com.jcortes.deco.client.BedrockTextClient
import com.jcortes.deco.content.model.ScrapedDocument
import com.jcortes.deco.util.Pageable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.services.bedrockruntime.model.ThrottlingException

@Service
class ScrapedDocumentService(
    private val bedrockTextClient: BedrockTextClient,
    private val scrapedDocumentRepository: ScrapedDocumentRepository,
    private val imageService: ImageService
) {

    @Lazy
    @Autowired
    private lateinit var self: ScrapedDocumentService

    fun enrich() {
        val documents = scrapedDocumentRepository.listWithoutEmbedding()
        documents.parallelStream().forEach { doc ->
            try{
                doc.resume = bedrockTextClient.invokeTextModel(userPrompt = doc.content, systemPrompt = DESCRIPTION_PROMPT)
                doc.keywords = bedrockTextClient.invokeTextModel(userPrompt = doc.content, systemPrompt = KEYWORDS_PROMPT) { extractKeywords(it) }
                doc.embedding = bedrockTextClient.invokeEmbeddingModel(userPrompt = doc.resume!!)
                self.save(doc)
            } catch (te: ThrottlingException) {
              Thread.sleep(20_000)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveImageDocuments() {
        val documents = scrapedDocumentRepository.iterate().asSequence().toList()
        documents.forEach { doc ->
            try{
                val regex = Regex("""https://content\.elmueble\.com\S+\.(?:jpg|jpeg|png|gif)""")
                val imageUrls = doc.content?.let { content -> regex.findAll(content).map { it.value }.toList() }
                val images = imageUrls?.parallelStream()?.map { imageService.download(it)}?.toList()

                images?.let { imageService.enrich(it) }

                doc.images = images
                self.save(doc)
            } catch (te: ThrottlingException) {
              Thread.sleep(20_000)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Transactional
    fun save(scrapedDocument: ScrapedDocument) {
        scrapedDocumentRepository.save(scrapedDocument)
    }

    fun search(query: String?, siteCategories: List<String>, pageable: Pageable): List<ScrapedDocument> {
        val embedding = query?.takeUnless { it.isBlank() }?.let { bedrockTextClient.invokeEmbeddingModel(userPrompt = it) }
        return scrapedDocumentRepository.search(embedding, siteCategories, pageable)
    }

    fun processedSourceIds(): List<String> {
        return scrapedDocumentRepository.listSourceIds()
    }

    private fun extractKeywords(text: String?): List<String> {
        return text?.split(",")?.map { it.trim() } ?: emptyList()
    }

    companion object {
        private const val DESCRIPTION_PROMPT =
            "Eres un sistema encargado de resumir artículos de un blog de decoración e interiorismo. Tu principal cometido es hacer un resumen claro y conciso de no más de 2048 caracteres para poder generar un embedding para realizar búsquedas semánticas de contenido. Ignorarás por completo los tags html que se pasen en la entrada y te centrarás solamente en el texto. Es importante que detalles qué tipo de artículos se detallan en los artículos para poder relacionar los artículos con imágenes en procesos posteriores."
        private const val KEYWORDS_PROMPT =
            "Describe keywords del artículo. Da prioridad a la zona de la casa (si aplica) y el resto que describan estilo decorativo, mobiliario, materiales y texturas. Solo retorna un listado separado por comas y todo en minúsculas."

    }
}