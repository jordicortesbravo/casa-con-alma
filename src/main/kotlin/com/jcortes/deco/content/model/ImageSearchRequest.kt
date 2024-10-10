package com.jcortes.deco.content.model

import com.jcortes.deco.content.EmbeddingType

data class ImageSearchRequest (
    val searchEmbedding: List<Float>? = null,
    val embeddingType: EmbeddingType? = EmbeddingType.TEXT,
    val keywords: List<String> = emptyList(),
    val minKeywordsMatch: Int = (keywords.size * 0.25).toInt(),
    val hasRights: Boolean? = null,
    val lightIntensity: Float? = 0.7f,
    val elegance: Float? = 0.6f,
    val pageSize: Int = 20,
    val pageNumber: Int = 1,
)