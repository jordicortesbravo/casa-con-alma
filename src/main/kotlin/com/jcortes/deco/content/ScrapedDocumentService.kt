package com.jcortes.deco.content

import com.jcortes.deco.content.model.ScrapedDocument
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ScrapedDocumentService(
    private val scrapedDocumentRepository: ScrapedDocumentRepository
) {

    @Transactional
    fun save(scrapedDocument: ScrapedDocument) {
        scrapedDocumentRepository.save(scrapedDocument)
    }

    fun processedSourceIds(): List<String> {
        return scrapedDocumentRepository.listSourceIds()
    }
}