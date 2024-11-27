package com.jcortes.deco.image.model

import com.jcortes.deco.image.EmbeddingType

data class ImageSearchRequest (
    val searchEmbedding: List<Float>? = null,
    val embeddingType: EmbeddingType? = EmbeddingType.TEXT,
    val keywords: List<String> = emptyList(),
    val minKeywordsMatch: Int = (keywords.size * 0.25).toInt(),
    val hasRights: Boolean? = false,
    val generatedByIA: Boolean? = true,
    val lightIntensity: Float? = 0.7f,
    val elegance: Float? = 0.6f,
    val pageSize: Int = 20,
    val pageNumber: Int = 1,
)