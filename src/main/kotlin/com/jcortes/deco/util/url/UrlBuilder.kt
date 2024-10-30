package com.jcortes.deco.util.url

class UrlBuilder(
    private val contentBaseUrl: String,
    private val imageBaseUrl: String,
    private val staticResourcesBaseUrl: String,
) {

    fun contentUrl(path: String?): String? {
        return path?.let { "$contentBaseUrl$path" }
    }

    fun imageUrl(path: String?): String? {
        return path?.let { "$imageBaseUrl$path" }
    }

    fun staticUrl(path: String?): String? {
        return path?.let { "$staticResourcesBaseUrl$path" }
    }

    fun contentUrl(): String = contentBaseUrl

    fun imageUrl(): String = imageBaseUrl

    fun staticUrl(): String = staticResourcesBaseUrl
}