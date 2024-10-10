package com.jcortes.deco.web.api

import com.jcortes.deco.content.ScrapedDocumentService
import com.jcortes.deco.content.model.ScrapedDocument
import com.jcortes.deco.util.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${app.base-path}/scraped-documents")
class ScrapedDocumentController(
    private val documentService: ScrapedDocumentService
) {

    @GetMapping("/enrich")
    fun enrich() {
        documentService.enrich()
    }

    @GetMapping("/search")
    fun search(
        @RequestParam query: String? = null,
        @RequestParam("category") category: String,
        @RequestParam sort: String? = null,
        @RequestParam page: Int = 1,
       @RequestParam pageSize: Int = 50
    ): SearchDocumentsResponse {
        val categories = if(category.isBlank()) emptyList() else listOf(category)
        return SearchDocumentsResponse(documentService.search(query, categories, Pageable(page, pageSize)))
    }

    @GetMapping("/save-images")
    fun saveImages() {
        documentService.saveImageDocuments()
    }

    data class SearchDocumentsResponse(val results: List<ScrapedDocument>)
}