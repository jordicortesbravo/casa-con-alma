package com.jcortes.deco.content.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.net.URI

class Image{
    var id: Long? = null
    lateinit var sourceId: String

    var keywords: List<String>? = null
    var description: String? = null
    var caption: String? = null
    var author: String? = null

    @JsonIgnore
    var embedding: List<Float>? = null
    @JsonIgnore
    var multimodalEmbedding: List<Float>? = null

    lateinit var sourceUrl: URI
    var url: URI? = null
    var seoUrl: URI? = null
    var internalUri: URI? = null
    var hasRights: Boolean = false
}
