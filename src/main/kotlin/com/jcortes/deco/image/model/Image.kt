package com.jcortes.deco.image.model

import com.fasterxml.jackson.databind.JsonNode
import java.time.Instant

enum class ImageStatus {
    READY_TO_PUBLISH, PUBLISHED, ARCHIVED
}

class Image {
    var id: Long? = null
    lateinit var sourceId: String

    var keywords: List<String>? = null
    var description: String? = null
    var caption: String? = null
    var author: String? = null

    var embedding: List<Float>? = null
    var multimodalEmbedding: List<Float>? = null

    var status: ImageStatus = ImageStatus.READY_TO_PUBLISH
    var publishInstant: Instant? = null

    lateinit var sourceUrl: String
    var url: String? = null
    var seoUrl: String? = null
    var internalUri: String? = null
    var hasRights: Boolean = false
    var characteristics: JsonNode? = null
    var iaGenerated: Boolean = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Image) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
