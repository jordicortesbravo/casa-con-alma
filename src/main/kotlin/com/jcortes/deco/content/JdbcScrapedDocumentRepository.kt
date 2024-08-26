package com.jcortes.deco.content

import com.fasterxml.jackson.databind.ObjectMapper
import com.jcortes.deco.content.model.ScrapedDocument
import com.jcortes.deco.content.model.SiteCategory
import com.jcortes.deco.util.ChunkIterator
import com.jcortes.deco.util.IdGenerator
import com.jcortes.deco.util.JdbcUtils
import org.postgresql.util.PGobject
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.sql.Types
import javax.sql.DataSource

data class ChunkIteratorState(
    val lastProcessedId: Long?,
    val prevElements: Int
)

@Repository
class JdbcScrapedDocumentRepository(
    private val dataSource: DataSource,
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val objectMapper: ObjectMapper,
    private val idGenerator: IdGenerator
) : ScrapedDocumentRepository {

    override fun get(id: Long): ScrapedDocument? {
        val query = """
            SELECT content
            FROM $TABLE_CONTENT
            WHERE id = :id
        """
        return jdbcTemplate.query(query, JdbcUtils.paramsOf("id" to id)) { rs, _ -> rs.getBinaryStream("content") }
            .firstOrNull()
            ?.let { objectMapper.readValue(it, ScrapedDocument::class.java) }
    }

    override fun get(sourceId: String): ScrapedDocument? {
        val query = """
            SELECT content
            FROM $TABLE_CONTENT
            WHERE source_id = :sourceId
        """
        return jdbcTemplate.query(query, JdbcUtils.paramsOf("sourceId" to sourceId)) { rs, _ -> rs.getBinaryStream("content") }
            .firstOrNull()
            ?.let { objectMapper.readValue(it, ScrapedDocument::class.java) }
    }

    override fun getAll(ids: List<Long>): List<ScrapedDocument> {
        if (ids.isEmpty()) return emptyList()
        val query = """
            SELECT content
            FROM $TABLE_CONTENT
            WHERE id IN (:ids)
        """
        return jdbcTemplate.query(query, JdbcUtils.paramsOf("ids" to ids)) { rs, _ -> rs.getBinaryStream("content") }
            .map { objectMapper.readValue(it, ScrapedDocument::class.java) }
    }

    override fun iterate(maxElements: Int, category: SiteCategory): Iterator<ScrapedDocument> {
        return ChunkIterator<ChunkIteratorState, ScrapedDocument>(
            next = { previousState ->
                if (previousState?.lastProcessedId != null && previousState.prevElements < maxElements) {
                    ChunkIterator.Chunk(null, null)
                } else {
                    val content = listBySiteCategory(previousState?.lastProcessedId ?: 0, maxElements, category)
                    ChunkIterator.Chunk(
                        ChunkIteratorState(content.lastOrNull()?.id, content.size),
                        content.takeUnless { it.isEmpty() }
                    )
                }
            },
            finalizer = {}
        )
    }

    override fun save(scrapedDocument: ScrapedDocument) {
        val previousScrapedDocument = get(requireNotNull(scrapedDocument.sourceId) { "sourceId is required" })
        scrapedDocument.id = previousScrapedDocument?.id ?: idGenerator.nextId()
        jdbcTemplate.update(SAVE_INDEX_QUERY, indexParamsOf(scrapedDocument))
        jdbcTemplate.update(SAVE_CONTENT_QUERY, contentParamsOf(scrapedDocument))
    }

    private fun listBySiteCategory(minId: Long, maxElements: Int, category: SiteCategory): List<ScrapedDocument> {
        val query = """
            SELECT id
            FROM $TABLE_INDEX
            WHERE id > :minId
              AND site_categories @> :category
            ORDER BY id
            LIMIT :maxElements
        """
        val params = MapSqlParameterSource()
        params.addValue("minId", minId)
        params.addValue("maxElements", maxElements)
        params.addValue("category", sqlArrayOf(listOf(category.name)), Types.ARRAY)
        val ids = jdbcTemplate.query(query, params) { rs, _ -> rs.getLong("id") }

        return getAll(ids)
    }

    private fun indexParamsOf(scrapedDocument: ScrapedDocument): MapSqlParameterSource {
        val params = MapSqlParameterSource()
        params.addValue("id", scrapedDocument.id)
        params.addValue("sourceId", scrapedDocument.sourceId)
        params.addValue("keywords", sqlArrayOf(scrapedDocument.keywords), Types.ARRAY)
        params.addValue("siteCategories", sqlArrayOf(scrapedDocument.siteCategories?.map { it.name }), Types.ARRAY)
        params.addValue("productCategories", sqlArrayOf(scrapedDocument.productCategories), Types.ARRAY)
        params.addValue("createInstant", Timestamp.from(scrapedDocument.createInstant))
        params.addValue("updateInstant", Timestamp.from(scrapedDocument.updateInstant))
        return params

    }

    private fun sqlArrayOf(data: List<String>?): java.sql.Array? {
        return data?.let { dataSource.connection.use { it.createArrayOf("TEXT", data.toTypedArray())} }
    }

    private fun contentParamsOf(scrapedDocument: ScrapedDocument): MapSqlParameterSource {
        val content = PGobject().apply {
            type = "jsonb"
            value = objectMapper.writeValueAsString(scrapedDocument)
        }
        return JdbcUtils.paramsOf("id" to scrapedDocument.id, "sourceId" to scrapedDocument.sourceId!!, "content" to content)
    }

    private companion object {
        private const val TABLE_INDEX = "deco.scraped_document_index"
        private const val SAVE_INDEX_QUERY = """INSERT INTO $TABLE_INDEX (id, source_id, keywords, site_categories, product_categories, create_instant, update_instant)
            VALUES (:id, :sourceId, :keywords, :siteCategories, :productCategories, :createInstant, :updateInstant)"""

        private const val TABLE_CONTENT = "deco.scraped_document_content"
        private const val SAVE_CONTENT_QUERY = """INSERT INTO $TABLE_CONTENT (id, source_id, content)
            VALUES (:id, :sourceId, :content)"""
    }
}