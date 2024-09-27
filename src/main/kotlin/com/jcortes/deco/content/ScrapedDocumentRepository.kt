package com.jcortes.deco.content

import com.jcortes.deco.content.model.ScrapedDocument
import com.jcortes.deco.content.model.SiteCategory
import com.jcortes.deco.util.Pageable

interface ScrapedDocumentRepository {

    fun get(id: Long): ScrapedDocument?
    fun get(sourceId: String): ScrapedDocument?
    fun getAll(ids: List<Long>): List<ScrapedDocument>
    fun list(ids: List<Long>): List<ScrapedDocument>
    fun listSourceIds(): List<String>
    fun search(searchEmbedding: List<Float>? = null, siteCategories: List<String>, pageable: Pageable): List<ScrapedDocument>
    fun iterate(maxElements: Int = 1000, category: SiteCategory): Iterator<ScrapedDocument>
    fun save(scrapedDocument: ScrapedDocument)
}