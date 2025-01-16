package com.jcortes.deco.image.infrastructure.jdbc

import com.fasterxml.jackson.databind.ObjectMapper
import com.jcortes.deco.image.EmbeddingType
import com.jcortes.deco.image.ImageRepository
import com.jcortes.deco.image.model.Image
import com.jcortes.deco.image.model.ImageSearchRequest
import com.jcortes.deco.tools.util.jdbc.IdRepository
import com.jcortes.deco.tools.util.jdbc.JdbcUtils
import com.jcortes.deco.tools.util.paging.ChunkIterator
import com.jcortes.deco.tools.util.paging.DefaultChunkIteratorState
import org.postgresql.util.PGobject
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Timestamp
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

    override fun getBySeoUrl(seoUrl: String): Image? {
        val query = """
            SELECT content
            FROM $TABLE_CONTENT
            WHERE seo_url = :seoUrl
        """
        return jdbcTemplate.query(query, JdbcUtils.paramsOf("seoUrl" to seoUrl)) { rs, _ -> rs.getBinaryStream("content") }
            .firstOrNull()
            ?.let { objectMapper.readValue(it, Image::class.java) }
    }

    override fun getEmbedding(id: Long, embeddingType: EmbeddingType): List<Float>? {
        val embeddingClause = if (embeddingType == EmbeddingType.MULTI_MODAL) "multimodal_embedding" else "embedding"
        val query = """
            SELECT $embeddingClause
            FROM $TABLE_INDEX
            WHERE id = :id
        """
        return jdbcTemplate.query(query, JdbcUtils.paramsOf("id" to id)) { rs, _ -> rs.getString(embeddingClause) }
            .firstOrNull()
            ?.let { it.removeSurrounding("[", "]")
                .split(",")
                .map { f -> f.toFloat() }
            }
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

    override fun search(request: ImageSearchRequest): List<Image> {
        val embeddingColumn = if (request.embeddingType == EmbeddingType.MULTI_MODAL) "multimodal_embedding" else "embedding"
        val hasRightsClause = request.hasRights?.let { " AND has_rights = :hasRights" } ?: ""
        val lightIntensityClause = " AND light_intensity >= :lightIntensity"
        val eleganceClause = " AND elegance >= :elegance"

        val keywordMatchClause = """(SELECT COUNT(*) FROM unnest(keywords) AS k WHERE k = ANY(:keywords)) >= :minMatches"""

        val query = """
            SELECT id
            FROM $TABLE_INDEX
            WHERE $keywordMatchClause $hasRightsClause $lightIntensityClause $eleganceClause
            ORDER BY $embeddingColumn <=> CAST(:embedding AS vector), elegance DESC, light_intensity DESC
            LIMIT :limit OFFSET :offset
        """

        val params = MapSqlParameterSource()
        request.searchEmbedding?.let { params.addValue("embedding", floatArrayOf(it), Types.ARRAY) }
        params.addValue("keywords", stringArrayOf(request.keywords), Types.ARRAY)
        params.addValue("minMatches", request.minKeywordsMatch)
        params.addValue("lightIntensity", request.lightIntensity)
        params.addValue("elegance", request.elegance)
        params.addValue("limit", request.pageSize)
        params.addValue("offset", request.pageNumber * request.pageSize)
        request.hasRights?.let { params.addValue("hasRights", it) }
        request.generatedByIA?.let { params.addValue("iaGenerated", it) }

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

    override fun iterate(): Iterator<Image> {
        return ChunkIterator<DefaultChunkIteratorState, Image>(
            next = { previousState ->
                if (previousState?.lastProcessedId != null && previousState.prevElements < 1_000) {
                    ChunkIterator.Chunk(null, null)
                } else {
                    val content = list(previousState?.lastProcessedId ?: 0, 1_000)
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

    private fun list(minId: Long, maxElements: Int): List<Image> {
        val query = """
            SELECT id
            FROM $TABLE_INDEX
            WHERE id > :minId
            ORDER BY id
            LIMIT :maxElements
        """
        val params = MapSqlParameterSource()
        params.addValue("minId", minId)
        params.addValue("maxElements", maxElements)

        val ids = jdbcTemplate.query(query, params) { rs, _ -> rs.getLong("id") }

        return list(ids)
    }

    private fun indexParamsOf(image: Image): MapSqlParameterSource {
        val params = MapSqlParameterSource()
        params.addValue("id", image.id)
        params.addValue("sourceId", image.sourceId)
        params.addValue("seoUrl", image.seoUrl)
        params.addValue("keywords", stringArrayOf(image.keywords), Types.ARRAY)
        params.addValue("lightIntensity", image.characteristics?.get("lightIntensity")?.asDouble())
        params.addValue("elegance", image.characteristics?.get("elegance")?.asDouble())
        params.addValue("hasRights", image.hasRights)
        params.addValue("iaGenerated", image.iaGenerated)
        params.addValue("embedding", floatArrayOf(image.embedding), Types.ARRAY)
        params.addValue("multimodalEmbedding", floatArrayOf(image.multimodalEmbedding), Types.ARRAY)
        params.addValue("status", image.status.name)
        params.addValue("publishInstant", image.publishInstant?.let { Timestamp.from(it) })
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
        return JdbcUtils.paramsOf(
            "id" to image.id,
            "sourceId" to image.sourceId,
            "seoUrl" to image.seoUrl,
            "content" to content
        )
    }

    private companion object {
        private const val TABLE_INDEX = "deco.image_index"
        private const val SAVE_INDEX_QUERY = """INSERT INTO $TABLE_INDEX (id, source_id, seo_url, keywords, has_rights, ia_generated, embedding, multimodal_embedding, light_intensity, elegance, status, publish_instant)
            VALUES (:id, :sourceId, :seoUrl, :keywords, :hasRights, :iaGenerated, :embedding, :multimodalEmbedding, :lightIntensity, :elegance, :status, :publishInstant)
            ON CONFLICT (id) DO UPDATE
            SET source_id = :sourceId, seo_url = :seoUrl, keywords = :keywords, has_rights= :hasRights, ia_generated = :iaGenerated, embedding = :embedding, multimodal_embedding = :multimodalEmbedding, light_intensity = :lightIntensity, elegance = :elegance, status = :status, publish_instant = :publishInstant"""

        private const val TABLE_CONTENT = "deco.image_content"
        private const val SAVE_CONTENT_QUERY = """INSERT INTO $TABLE_CONTENT (id, source_id, seo_url, content)
            VALUES (:id, :sourceId, :seoUrl, :content)
            ON CONFLICT (id) DO UPDATE
            SET source_id = :sourceId, seo_url = :seoUrl, content = :content"""
    }
}