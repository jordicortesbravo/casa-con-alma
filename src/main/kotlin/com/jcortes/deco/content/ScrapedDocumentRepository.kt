package com.jcortes.deco.content

import com.jcortes.deco.content.model.ScrapedDocument

interface ScrapedDocumentRepository {

    fun get(id: Long): ScrapedDocument?
    fun get(sourceId: String): ScrapedDocument?
    fun save(scrapedDocument: ScrapedDocument)
}