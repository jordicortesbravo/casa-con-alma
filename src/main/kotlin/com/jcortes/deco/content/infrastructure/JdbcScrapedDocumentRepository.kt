package com.jcortes.deco.content.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import com.jcortes.deco.content.ScrapedDocumentRepository
import com.jcortes.deco.content.model.ScrapedDocument
import com.jcortes.deco.content.model.SiteCategory
import com.jcortes.deco.util.ChunkIterator
import com.jcortes.deco.util.DefaultChunkIteratorState
import com.jcortes.deco.util.JdbcUtils
import com.jcortes.deco.util.Pageable
import org.postgresql.util.PGobject
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.sql.Types
import javax.sql.DataSource

@Repository
class JdbcScrapedDocumentRepository(
    private val dataSource: DataSource,
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val objectMapper: ObjectMapper,
    private val idRepository: IdRepository
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

    override fun list(ids: List<Long>): List<ScrapedDocument> {
        if (ids.isEmpty()) return emptyList()
        val query = """
            SELECT content
            FROM $TABLE_CONTENT
            WHERE id IN (:ids)
        """
        return jdbcTemplate.query(query, JdbcUtils.paramsOf("ids" to ids)) { rs, _ -> rs.getBinaryStream("content") }
            .map { objectMapper.readValue(it, ScrapedDocument::class.java) }
    }

    override fun listSourceIds(): List<String> {
        val query = """
            SELECT source_id
            FROM $TABLE_INDEX
        """
        return jdbcTemplate.query(query) { rs, _ -> rs.getString("source_id") }
    }

    override fun listWithoutEmbedding(): List<ScrapedDocument> {
        val query = """
            SELECT id
            FROM $TABLE_INDEX
            WHERE embedding IS NULL
            ORDER BY id DESC
            """
        val ids = jdbcTemplate.query(query) { rs, _ -> rs.getLong("id") }
        return list(ids)
    }

    override fun search(searchEmbedding: List<Float>?, siteCategories: List<String>, pageable: Pageable): List<ScrapedDocument> {
        val embeddingClause = searchEmbedding?.let { " AND embedding <=> CAST(:embedding AS vector) < :threshold" } ?: ""
        val orderClause = searchEmbedding?.let { "embedding <=> CAST(:embedding AS vector), id DESC" } ?: "id DESC"
        val query = """
            SELECT id
            FROM $TABLE_INDEX
            WHERE site_categories @> :siteCategories $embeddingClause
            ORDER BY $orderClause
            LIMIT :limit OFFSET :offset
        """

        val params = MapSqlParameterSource()
        searchEmbedding?.let {
            params.addValue("embedding", floatArrayOf(searchEmbedding), Types.ARRAY)
            params.addValue("threshold", 0.5)
        }
        params.addValue("siteCategories", stringArrayOf(siteCategories), Types.ARRAY)
        params.addValue("limit", pageable.pageSize)
        params.addValue("offset", pageable.pageNumber * pageable.pageSize)

        val ids = jdbcTemplate.query(query, params) { rs, _ -> rs.getLong("id") }
        return list(ids)
    }

    override fun iterate(maxElements: Int, category: SiteCategory?): Iterator<ScrapedDocument> {
        return ChunkIterator<DefaultChunkIteratorState, ScrapedDocument>(
            next = { previousState ->
                if (previousState?.lastProcessedId != null && previousState.prevElements < maxElements) {
                    ChunkIterator.Chunk(null, null)
                } else {
                    val content = list(previousState?.lastProcessedId ?: 0, maxElements, category)
                    ChunkIterator.Chunk(
                        DefaultChunkIteratorState(content.lastOrNull()?.id, content.size),
                        content.takeUnless { it.isEmpty() }
                    )
                }
            },
            finalizer = {}
        )
    }

    override fun save(scrapedDocument: ScrapedDocument) {
        val previousScrapedDocument = get(requireNotNull(scrapedDocument.sourceId) { "sourceId is required" })
        scrapedDocument.id = previousScrapedDocument?.id ?: idRepository.nextId()
        jdbcTemplate.update(SAVE_INDEX_QUERY, indexParamsOf(scrapedDocument))
        jdbcTemplate.update(SAVE_CONTENT_QUERY, contentParamsOf(scrapedDocument))
    }

    private fun list(minId: Long, maxElements: Int, category: SiteCategory?): List<ScrapedDocument> {
        val categoryClause = category?.let { " AND site_categories @> :category" } ?: ""
        val query = """
            SELECT id
            FROM $TABLE_INDEX
            WHERE id > :minId $categoryClause
            ORDER BY id
            LIMIT :maxElements
        """
        val params = MapSqlParameterSource()
        params.addValue("minId", minId)
        params.addValue("maxElements", maxElements)
        category?.let { params.addValue("category", stringArrayOf(listOf(category.name)), Types.ARRAY) }
        val ids = jdbcTemplate.query(query, params) { rs, _ -> rs.getLong("id") }

        return getAll(ids)
    }

    private fun indexParamsOf(scrapedDocument: ScrapedDocument): MapSqlParameterSource {
        val params = MapSqlParameterSource()
        params.addValue("id", scrapedDocument.id)
        params.addValue("sourceId", scrapedDocument.sourceId)
        params.addValue("keywords", stringArrayOf(scrapedDocument.keywords), Types.ARRAY)
        params.addValue("siteCategories", stringArrayOf(scrapedDocument.siteCategories?.map { it.name }), Types.ARRAY)
        params.addValue("productCategories", stringArrayOf(scrapedDocument.productCategories), Types.ARRAY)
        params.addValue("createInstant", Timestamp.from(scrapedDocument.createInstant))
        params.addValue("updateInstant", Timestamp.from(scrapedDocument.updateInstant))
        params.addValue("embedding", floatArrayOf(scrapedDocument.embedding), Types.ARRAY)
        return params

    }

    private fun stringArrayOf(data: List<String>?): java.sql.Array? {
        return data?.let { dataSource.connection.use { it.createArrayOf("TEXT", data.toTypedArray()) } }
    }

    private fun floatArrayOf(data: List<Float>?): java.sql.Array? {
        return data?.let { dataSource.connection.use { it.createArrayOf("FLOAT", data.toTypedArray()) } }
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
        private const val SAVE_INDEX_QUERY = """INSERT INTO $TABLE_INDEX (id, source_id, keywords, site_categories, product_categories, create_instant, update_instant, embedding)
            VALUES (:id, :sourceId, :keywords, :siteCategories, :productCategories, :createInstant, :updateInstant, :embedding)
            ON CONFLICT (id) DO UPDATE
            SET source_id = :sourceId, keywords = :keywords, site_categories = :siteCategories, product_categories = :productCategories, create_instant = :createInstant, update_instant = :updateInstant, embedding = :embedding"""

        private const val TABLE_CONTENT = "deco.scraped_document_content"
        private const val SAVE_CONTENT_QUERY = """INSERT INTO $TABLE_CONTENT (id, source_id, content)
            VALUES (:id, :sourceId, :content)
            ON CONFLICT (id) DO UPDATE
            SET source_id = :sourceId, content = :content"""
    }
}