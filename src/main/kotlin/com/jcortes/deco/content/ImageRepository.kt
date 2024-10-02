package com.jcortes.deco.content

import com.jcortes.deco.content.model.Image
import com.jcortes.deco.util.Pageable

enum class EmbeddingType {
    MULTI_MODAL, TEXT
}

interface ImageRepository {

    fun get(id: Long): Image?
    fun get(sourceId: String): Image?
    fun getEmbedding(id: Long, embeddingType: EmbeddingType): List<Float>?
    fun list(ids: List<Long>): List<Image>
    fun search(searchEmbedding: List<Float>? = null, embeddingType: EmbeddingType, keywords: List<String>, hasRights: Boolean? = false, pageable: Pageable): List<Image>
    fun listSourceIds(): List<String>
    fun iterate(): Iterator<Image>
    fun save(image: Image)
    fun save(images: List<Image>)
}