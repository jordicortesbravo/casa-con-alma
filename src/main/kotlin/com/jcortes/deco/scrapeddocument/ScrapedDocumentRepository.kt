package com.jcortes.deco.scrapeddocument

import com.jcortes.deco.scrapeddocument.model.ScrapedDocument
import com.jcortes.deco.tools.util.paging.Pageable

interface ScrapedDocumentRepository {

    fun get(id: Long): ScrapedDocument?
    fun get(sourceId: String): ScrapedDocument?
    fun list(ids: List<Long>): List<ScrapedDocument>
    fun listWithoutEmbedding(): List<ScrapedDocument>
    fun listSourceIds(): List<String>
    fun search(searchEmbedding: List<Float>? = null, siteCategories: List<String>, pageable: Pageable): List<ScrapedDocument>
    fun iterate(): Iterator<ScrapedDocument>
    fun save(scrapedDocument: ScrapedDocument)
}