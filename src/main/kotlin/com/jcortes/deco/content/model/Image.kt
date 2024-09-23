package com.jcortes.deco.content.model

import java.net.URI

class Image{
    var id: Long? = null
    lateinit var sourceId: String

    var keywords: List<String>? = null
    var description: String? = null
    var caption: String? = null
    var embedding: List<Float>? = null
    var author: String? = null

    lateinit var sourceUrl: URI
    var url: URI? = null
    var seoUrl: URI? = null
    var internalUri: URI? = null
}
