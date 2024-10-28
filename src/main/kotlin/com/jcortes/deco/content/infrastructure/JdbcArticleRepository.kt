package com.jcortes.deco.content.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import com.jcortes.deco.content.ArticleRepository
import com.jcortes.deco.content.ImageRepository
import com.jcortes.deco.content.model.Article
import com.jcortes.deco.content.model.ArticleSearchRequest
import com.jcortes.deco.content.model.Image
import com.jcortes.deco.util.ChunkIterator
import com.jcortes.deco.util.DefaultChunkIteratorState
import com.jcortes.deco.util.JdbcUtils
import org.postgresql.util.PGobject
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.sql.Types
import java.time.Instant
import javax.sql.DataSource

@Repository
class JdbcArticleRepository(
    private val dataSource: DataSource,
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val objectMapper: ObjectMapper,
    private val idRepository: IdRepository,
    private val imageRepository: ImageRepository
) : ArticleRepository {

    override fun get(id: Long): Article? {
        val query = """
            SELECT content
            FROM $TABLE_CONTENT
            WHERE id = :id
        """
        return jdbcTemplate.query(query, JdbcUtils.paramsOf("id" to id)) { rs, _ -> rs.getBinaryStream("content") }
            .firstOrNull()
            ?.let {
                val doc = objectMapper.readValue(it, Article::class.java)
                doc.images = listImages(doc)
                doc
            }
    }

    override fun getBySeoUrl(seoUrl: String): Article? {
        val query = """
            SELECT content
            FROM $TABLE_CONTENT
            WHERE seo_url = :seoUrl
        """
        return jdbcTemplate.query(query, JdbcUtils.paramsOf("seoUrl" to seoUrl)) { rs, _ -> rs.getBinaryStream("content") }
            .firstOrNull()
            ?.let {
                val doc = objectMapper.readValue(it, Article::class.java)
                doc.images = listImages(doc)
                doc
            }
    }

    override fun list(ids: List<Long>): List<Article> {
        if (ids.isEmpty()) return emptyList()
        val query = """
            SELECT content
            FROM $TABLE_CONTENT
            WHERE id IN (:ids)
        """
        return jdbcTemplate.query(query, JdbcUtils.paramsOf("ids" to ids)) { rs, _ -> rs.getBinaryStream("content") }
            .filterNotNull()
            .map {
                val doc = objectMapper.readValue(it, Article::class.java)
                doc.images = listImages(doc)
                doc
            }
    }


    override fun search(request: ArticleSearchRequest): List<Article> {
        val categoryClause = request.siteCategories?.takeUnless { it.isEmpty() }?.let { "site_categories @> :siteCategories" } ?: "TRUE"
        val embeddingClause = request.embedding?.let { " AND embedding <=> CAST(:embedding AS vector) < :threshold" } ?: ""
        val tagsClause = request.tags?.takeUnless { it.isEmpty() }?.let { " AND tags @> :tags" } ?: ""
        val excludedIdsClause = request.excludedIds?.takeUnless { it.isEmpty() }?.let { " AND id NOT IN (:excludedIds)" } ?: ""
        val statusClause = request.status?.let { " AND status = :status" } ?: ""
        val orderClause = request.embedding?.let { "embedding <=> CAST(:embedding AS vector), update_instant DESC" } ?: "update_instant DESC"
        val query = """
            SELECT id
            FROM $TABLE_INDEX
            WHERE $categoryClause $embeddingClause $tagsClause $excludedIdsClause $statusClause
            ORDER BY $orderClause
            LIMIT :limit OFFSET :offset
        """

        val params = MapSqlParameterSource()
        request.embedding?.let {
            params.addValue("embedding", floatArrayOf(request.embedding), Types.ARRAY)
            params.addValue("threshold", 0.5)
        }
        request.excludedIds?.let { params.addValue("excludedIds", request.excludedIds) }
        params.addValue("siteCategories", stringArrayOf(request.siteCategories), Types.ARRAY)
        params.addValue("tags", stringArrayOf(request.tags?.map { it.name }), Types.ARRAY)
        params.addValue("status", request.status?.name)
        params.addValue("limit", request.pageSize)
        params.addValue("offset", (request.pageNumber * request.pageSize) + 1)

        val ids = jdbcTemplate.query(query, params) { rs, _ -> rs.getLong("id") }
        return list(ids)
    }

    override fun iterate(): Iterator<Article> {
        return ChunkIterator<DefaultChunkIteratorState, Article>(
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

    override fun save(article: Article) {
        if (article.id == null) {
            article.id = idRepository.nextId()
        }
        article.updateInstant = Instant.now()
        jdbcTemplate.update(SAVE_INDEX_QUERY, indexParamsOf(article))
        jdbcTemplate.update(SAVE_CONTENT_QUERY, contentParamsOf(article))

        article.images?.forEach { image ->
            jdbcTemplate.update(SAVE_DOC_IMAGE_QUERY, JdbcUtils.paramsOf("docId" to article.id, "imageId" to image.id))
        }
    }

    private fun list(minId: Long, maxElements: Int): List<Article> {
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

    private fun listImages(article: Article): List<Image> {
        val query = """
            SELECT image_id
            FROM $TABLE_DOC_IMAGE
            WHERE article_id = :docId
        """
        return jdbcTemplate.query(query, JdbcUtils.paramsOf("docId" to article.id!!)) { rs, _ ->
            rs.getLong("image_id")
        }.mapNotNull { imageId -> imageRepository.get(imageId) }
    }

    private fun indexParamsOf(article: Article): MapSqlParameterSource {
        val params = MapSqlParameterSource()
        params.addValue("id", article.id)
        params.addValue("seoUrl", article.seoUrl)
        params.addValue("keywords", stringArrayOf(article.keywords), Types.ARRAY)
        params.addValue("tags", stringArrayOf(article.tags?.map { it.name }), Types.ARRAY)
        params.addValue("siteCategories", stringArrayOf(article.siteCategories?.map { it.name }), Types.ARRAY)
        params.addValue("productCategories", stringArrayOf(article.productCategories), Types.ARRAY)
        params.addValue("status", article.status.name)
        params.addValue("createInstant", Timestamp.from(article.createInstant))
        params.addValue("updateInstant", Timestamp.from(article.updateInstant))
        params.addValue("publishInstant", article.publishInstant?.let { Timestamp.from(it) })
        params.addValue("embedding", floatArrayOf(article.embedding), Types.ARRAY)
        return params

    }

    private fun stringArrayOf(data: List<String>?): java.sql.Array? {
        return data?.let { dataSource.connection.use { it.createArrayOf("TEXT", data.toTypedArray()) } }
    }

    private fun floatArrayOf(data: List<Float>?): java.sql.Array? {
        return data?.let { dataSource.connection.use { it.createArrayOf("FLOAT", data.toTypedArray()) } }
    }

    private fun contentParamsOf(article: Article): MapSqlParameterSource {
        val content = PGobject().apply {
            type = "jsonb"
            value = objectMapper.writeValueAsString(article)
        }
        return JdbcUtils.paramsOf(
            "id" to article.id,
            "seoUrl" to article.seoUrl,
            "content" to content
        )
    }

    private companion object {
        private const val TABLE_INDEX = "deco.article_index"
        private const val SAVE_INDEX_QUERY =
            """INSERT INTO $TABLE_INDEX (id, seo_url, keywords, tags, site_categories, product_categories, status, create_instant, update_instant, publish_instant, embedding)
            VALUES (:id, :seoUrl, :keywords, :tags, :siteCategories, :productCategories, :status, :createInstant, :updateInstant, :publishInstant, :embedding)
            ON CONFLICT (id) DO UPDATE
            SET seo_url = :seoUrl, keywords = :keywords, tags = :tags, site_categories = :siteCategories, product_categories = :productCategories, status = :status, create_instant = :createInstant, update_instant = :updateInstant, publish_instant = :publishInstant, embedding = :embedding"""

        private const val TABLE_CONTENT = "deco.article_content"
        private const val SAVE_CONTENT_QUERY = """INSERT INTO $TABLE_CONTENT (id, seo_url, content)
            VALUES (:id, :seoUrl, :content)
            ON CONFLICT (id) DO UPDATE
            SET seo_url = :seoUrl, content = :content"""

        private const val TABLE_DOC_IMAGE = "deco.article_image"
        private const val SAVE_DOC_IMAGE_QUERY = """INSERT INTO $TABLE_DOC_IMAGE (article_id, image_id)
            VALUES (:docId, :imageId)"""
    }
}