package com.jcortes.deco.content

import com.jcortes.deco.client.BedrockClient
import com.jcortes.deco.content.model.ScrapedDocument
import com.jcortes.deco.util.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ScrapedDocumentService(
    private val bedrockClient: BedrockClient,
    private val scrapedDocumentRepository: ScrapedDocumentRepository
) {

    @Transactional
    fun save(scrapedDocument: ScrapedDocument) {
        scrapedDocumentRepository.save(scrapedDocument)
    }

    fun search(query: String?, siteCategories: List<String>, pageable: Pageable): List<ScrapedDocument> {
        return scrapedDocumentRepository.search(null, siteCategories, pageable)
    }

    fun processedSourceIds(): List<String> {
        return scrapedDocumentRepository.listSourceIds()
    }
}