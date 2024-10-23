package com.jcortes.deco.util

class UrlBuilder(
    private val contentBaseUrl: String,
    private val staticBaseUrl: String
) {

    fun contentUrl(path: String?): String? {
        return path?.let { "$contentBaseUrl$path" }
    }

    fun staticUrl(path: String?): String? {
        return path?.let { "$staticBaseUrl$path" }
    }

    fun staticUrl(): String = staticBaseUrl

    fun contentUrl(): String = contentBaseUrl
}