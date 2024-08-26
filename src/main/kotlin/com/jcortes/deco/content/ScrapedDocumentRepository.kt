package com.jcortes.deco.content

import com.jcortes.deco.content.model.ScrapedDocument
import com.jcortes.deco.content.model.SiteCategory

interface ScrapedDocumentRepository {

    fun get(id: Long): ScrapedDocument?
    fun get(sourceId: String): ScrapedDocument?
    fun getAll(ids: List<Long>): List<ScrapedDocument>
    fun iterate(maxElements: Int = 1000, category: SiteCategory): Iterator<ScrapedDocument>
    fun save(scrapedDocument: ScrapedDocument)
}