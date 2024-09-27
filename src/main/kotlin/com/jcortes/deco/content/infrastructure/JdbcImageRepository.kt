package com.jcortes.deco.content.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import com.jcortes.deco.content.ImageRepository
import com.jcortes.deco.content.model.Image
import com.jcortes.deco.util.*
import org.postgresql.util.PGobject
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Types
import javax.sql.DataSource

@Repository
class JdbcImageRepository(
    private val dataSource: DataSource,
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val objectMapper: ObjectMapper,
    private val idRepository: IdRepository
) : ImageRepository {

    override fun get(id: Long): Image? {
        val query = """
            SELECT content
            FROM $TABLE_CONTENT
            WHERE id = :id
        """
        return jdbcTemplate.query(query, JdbcUtils.paramsOf("id" to id)) { rs, _ -> rs.getBinaryStream("content") }
            .firstOrNull()
            ?.let { objectMapper.readValue(it, Image::class.java) }
    }

    override fun get(sourceId: String): Image? {
        val query = """
            SELECT content
            FROM $TABLE_CONTENT
            WHERE source_id = :sourceId
        """
        return jdbcTemplate.query(query, JdbcUtils.paramsOf("sourceId" to sourceId)) { rs, _ -> rs.getBinaryStream("content") }
            .firstOrNull()
            ?.let { objectMapper.readValue(it, Image::class.java) }
    }

    override fun list(ids: List<Long>): List<Image> {
        if (ids.isEmpty()) return emptyList()
        val query = """
            SELECT content
            FROM $TABLE_CONTENT
            WHERE id IN (:ids)
        """
        return jdbcTemplate.query(query, JdbcUtils.paramsOf("ids" to ids)) { rs, _ -> rs.getBinaryStream("content") }
            .map { objectMapper.readValue(it, Image::class.java) }
    }

    override fun search(searchEmbedding: List<Float>?, keywords: List<String>, pageable: Pageable): List<Image> {
        val embeddingClause = searchEmbedding?.let { " AND embedding <=> CAST(:embedding AS vector) < :threshold" } ?: ""
        val orderClause = searchEmbedding?.let { "embedding <=> CAST(:embedding AS vector), source_id" } ?: "source_id"
        val query = """
            SELECT id
            FROM $TABLE_INDEX
            WHERE keywords @> :keywords $embeddingClause
            ORDER BY $orderClause
            LIMIT :limit OFFSET :offset
        """

        val params = MapSqlParameterSource()
        searchEmbedding?.let {
            params.addValue("embedding", floatArrayOf(searchEmbedding), Types.ARRAY)
            params.addValue("threshold", 0.8)
        }
        params.addValue("keywords", stringArrayOf(keywords), Types.ARRAY)
        params.addValue("limit", pageable.pageSize)
        params.addValue("offset", pageable.pageNumber * pageable.pageSize)

        val ids = jdbcTemplate.query(query, params) { rs, _ -> rs.getLong("id") }
        return list(ids)
    }

    override fun listSourceIds(): List<String> {
        val query = """
            SELECT source_id
            FROM $TABLE_INDEX
        """
        return jdbcTemplate.query(query) { rs, _ -> rs.getString("source_id") }
    }

    override fun iterate(maxElements: Int, keyword: String, embedding: List<Float>?): Iterator<Image> {
        return ChunkIterator<DefaultChunkIteratorState, Image>(
            next = { previousState ->
                if (previousState?.lastProcessedId != null && previousState.prevElements < maxElements) {
                    ChunkIterator.Chunk(null, null)
                } else {
                    val content = listByKeyword(previousState?.lastProcessedId ?: 0, maxElements, keyword, embedding)
                    ChunkIterator.Chunk(
                        DefaultChunkIteratorState(content.lastOrNull()?.id, content.size),
                        content.takeUnless { it.isEmpty() }
                    )
                }
            },
            finalizer = {}
        )
    }

    override fun save(image: Image) {
        val previousImage = get(requireNotNull(image.sourceId) { "sourceId is required" })
        image.id = previousImage?.id ?: idRepository.nextId()
        jdbcTemplate.update(SAVE_INDEX_QUERY, indexParamsOf(image))
        jdbcTemplate.update(SAVE_CONTENT_QUERY, contentParamsOf(image))
    }

    override fun save(images: List<Image>) {
        val parameters = images.map { indexParamsOf(it) }
        jdbcTemplate.batchUpdate(SAVE_INDEX_QUERY, parameters.toTypedArray())

        val contentParameters = images.map { contentParamsOf(it) }
        jdbcTemplate.batchUpdate(SAVE_CONTENT_QUERY, contentParameters.toTypedArray())
    }

    private fun listByKeyword(minId: Long, maxElements: Int, keyword: String, embedding: List<Float>?): List<Image> {
        val orderClause = embedding?.let { "embedding <=> :embedding, id" } ?: "id"
        val query = """
            SELECT id
            FROM $TABLE_INDEX
            WHERE id > :minId
              AND keywords @> :keyword
            ORDER BY $orderClause
            LIMIT :maxElements
        """
        val params = MapSqlParameterSource()
        params.addValue("minId", minId)
        params.addValue("maxElements", maxElements)
        params.addValue("keyword", stringArrayOf(listOf(keyword)), Types.ARRAY)
        embedding?.let { params.addValue("embedding", floatArrayOf(embedding), Types.ARRAY) }

        val ids = jdbcTemplate.query(query, params) { rs, _ -> rs.getLong("id") }

        return list(ids)
    }

    private fun indexParamsOf(image: Image): MapSqlParameterSource {
        val params = MapSqlParameterSource()
        params.addValue("id", image.id)
        params.addValue("sourceId", image.sourceId)
        params.addValue("keywords", stringArrayOf(image.keywords), Types.ARRAY)
        params.addValue("embedding", floatArrayOf(image.embedding), Types.ARRAY)
        return params

    }

    private fun stringArrayOf(data: List<String>?): java.sql.Array? {
        return data?.let { dataSource.connection.use { it.createArrayOf("TEXT", data.toTypedArray()) } }
    }

    private fun floatArrayOf(data: List<Float>?): java.sql.Array? {
        return data?.let { dataSource.connection.use { it.createArrayOf("FLOAT", data.toTypedArray()) } }
    }

    private fun contentParamsOf(image: Image): MapSqlParameterSource {
        val content = PGobject().apply {
            type = "jsonb"
            value = objectMapper.writeValueAsString(image)
        }
        return JdbcUtils.paramsOf("id" to image.id, "sourceId" to image.sourceId, "content" to content)
    }

    private companion object {
        private const val TABLE_INDEX = "deco.image_index"
        private const val SAVE_INDEX_QUERY = """INSERT INTO $TABLE_INDEX (id, source_id, keywords, embedding)
            VALUES (:id, :sourceId, :keywords, :embedding)
            ON CONFLICT (id) DO UPDATE
            SET source_id = :sourceId, keywords = :keywords, embedding = :embedding"""

        private const val TABLE_CONTENT = "deco.image_content"
        private const val SAVE_CONTENT_QUERY = """INSERT INTO $TABLE_CONTENT (id, source_id, content)
            VALUES (:id, :sourceId, :content)
            ON CONFLICT (id) DO UPDATE
            SET source_id = :sourceId, content = :content"""
    }
}