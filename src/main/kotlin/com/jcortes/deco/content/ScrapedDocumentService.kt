package com.jcortes.deco.content

import com.jcortes.deco.client.BedrockDocumentClient
import com.jcortes.deco.content.model.ScrapedDocument
import com.jcortes.deco.util.Pageable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.services.bedrockruntime.model.ThrottlingException

@Service
class ScrapedDocumentService(
    private val bedrockDocumentClient: BedrockDocumentClient,
    private val scrapedDocumentRepository: ScrapedDocumentRepository
) {

    @Lazy
    @Autowired
    private lateinit var self: ScrapedDocumentService

    fun enrich() {
        val documents = scrapedDocumentRepository.listWithoutEmbedding()
        documents.parallelStream().forEach { doc ->
            try{
                doc.resume = bedrockDocumentClient.describe(doc)
                doc.keywords = bedrockDocumentClient.keywordsOf(doc)
                doc.embedding = bedrockDocumentClient.embeddingsOf(doc)
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
        val embedding = query?.takeUnless { it.isBlank() }?.let { bedrockDocumentClient.embeddingsOf(it) }
        return scrapedDocumentRepository.search(embedding, siteCategories, pageable)
    }

    fun processedSourceIds(): List<String> {
        return scrapedDocumentRepository.listSourceIds()
    }
}