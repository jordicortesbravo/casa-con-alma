package com.jcortes.deco.web

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

    @GetMapping("/search")
    fun search(
        @RequestParam query: String? = null,
        @RequestParam("site_categories") siteCategories: List<String> = emptyList(),
        @RequestParam sort: String? = null,
        @RequestParam page: Int = 1,
       @RequestParam pageSize: Int = 50
    ): SearchDocumentsResponse {

        return SearchDocumentsResponse(documentService.search(query, siteCategories, Pageable(page, pageSize)))
    }

    data class SearchDocumentsResponse(val results: List<ScrapedDocument>)

}