package com.jcortes.deco.content.model

import com.fasterxml.jackson.databind.JsonNode

class Image {
    var id: Long? = null
    lateinit var sourceId: String

    var keywords: List<String>? = null
    var description: String? = null
    var caption: String? = null
    var author: String? = null

    var embedding: List<Float>? = null
    var multimodalEmbedding: List<Float>? = null

    lateinit var sourceUrl: String
    var url: String? = null
    var seoUrl: String? = null
    var internalUri: String? = null
    var hasRights: Boolean = false
    var characteristics: JsonNode? = null
    var iaGenerated: Boolean = false
}
