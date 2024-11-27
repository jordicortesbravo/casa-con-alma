package com.jcortes.deco.image

import com.jcortes.deco.image.model.Image
import com.jcortes.deco.image.model.ImageSearchRequest

enum class EmbeddingType {
    MULTI_MODAL, TEXT
}

interface ImageRepository {

    fun get(id: Long): Image?
    fun get(sourceId: String): Image?
    fun getBySeoUrl(seoUrl: String): Image?
    fun getEmbedding(id: Long, embeddingType: EmbeddingType): List<Float>?
    fun list(ids: List<Long>): List<Image>
    fun search(request: ImageSearchRequest): List<Image>
    fun listSourceIds(): List<String>
    fun iterate(): Iterator<Image>
    fun save(image: Image)
    fun save(images: List<Image>)
}