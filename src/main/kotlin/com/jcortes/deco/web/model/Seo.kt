package com.jcortes.deco.web.model

data class Seo (
        val description: String,
        val keywords: String,
        val charset: String = Charsets.UTF_8.toString(),
        val viewport: String = "width=device-width, initial-scale=1",
        val robots: String = "index, follow",
        val author: String = "Casa con Alma",
        val socialNetworkTags: SocialNetworkTags,
        val twitterCard: TwitterCard,
        val canonicalUrl: String,
        val language: String = "es"
    )