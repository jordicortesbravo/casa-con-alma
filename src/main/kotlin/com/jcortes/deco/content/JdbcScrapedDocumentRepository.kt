package com.jcortes.deco.content

import com.fasterxml.jackson.databind.ObjectMapper
import com.jcortes.deco.content.model.ScrapedDocument
import com.jcortes.deco.util.IdGenerator
import com.jcortes.deco.util.JdbcUtils
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
class JdbcScrapedDocumentRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val objectMapper: ObjectMapper,
    private val idGenerator: IdGenerator
): ScrapedDocumentRepository {

    override fun get(id: Long): ScrapedDocument? {
        val query = """
            SELECT content
            FROM $TABLE_CONTENT
            WHERE id = :id
        """
        return jdbcTemplate.query(query, JdbcUtils.paramsOf("id" to id)) { rs, _ -> rs.getBinaryStream("content") }
            .firstOrNull()
            ?.let { objectMapper.readValue(it, ScrapedDocument::class.java)}
    }

    override fun get(sourceId: String): ScrapedDocument? {
        val query = """
            SELECT content
            FROM $TABLE_CONTENT
            WHERE source_id = :sourceId
        """
        return jdbcTemplate.query(query, JdbcUtils.paramsOf("sourceId" to sourceId)) { rs, _ -> rs.getBinaryStream("content") }
            .firstOrNull()
            ?.let { objectMapper.readValue(it, ScrapedDocument::class.java)}
    }

    override fun save(scrapedDocument: ScrapedDocument) {
        val previousScrapedDocument = get(requireNotNull(scrapedDocument.sourceId){"sourceId is required"})
        scrapedDocument.id = previousScrapedDocument?.id ?: idGenerator.nextId()
        jdbcTemplate.update(SAVE_INDEX_QUERY, indexParamsOf(scrapedDocument))
        jdbcTemplate.update(SAVE_CONTENT_QUERY, contentParamsOf(scrapedDocument))
    }

    private fun indexParamsOf(scrapedDocument: ScrapedDocument): MapSqlParameterSource = JdbcUtils.paramsOf(
        "id" to scrapedDocument.id,
        "sourceId" to scrapedDocument.sourceId,
        "keywords" to scrapedDocument.keywords,
        "siteCategories" to scrapedDocument.siteCategories?.map { it.name },
        "productCategories" to scrapedDocument.productCategories,
        "createInstant" to Timestamp.from(scrapedDocument.createInstant),
        "updateInstant" to Timestamp.from(scrapedDocument.updateInstant)
    )

    private fun contentParamsOf(scrapedDocument: ScrapedDocument): MapSqlParameterSource = JdbcUtils.paramsOf(
        "id" to scrapedDocument.id,
        "content" to scrapedDocument.content
    )

    private companion object {
        private const val TABLE_INDEX = "deco.scraped_document_index"
        private const val SAVE_INDEX_QUERY = """INSERT INTO $TABLE_INDEX (id, source_id, keywords, site_categories, product_categories, create_instant, update_instant)
            VALUES (:id, :sourceId, :keywords, :siteCategories, :productCategories, :createInstant, :updateInstant)"""

        private const val TABLE_CONTENT = "deco.scraped_document_content"
        private const val SAVE_CONTENT_QUERY = """INSERT INTO $TABLE_CONTENT (id, content)
            VALUES (:id, :content)"""
    }
}