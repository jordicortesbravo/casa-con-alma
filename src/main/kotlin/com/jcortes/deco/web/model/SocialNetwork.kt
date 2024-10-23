package com.jcortes.deco.web.model

data class SocialNetworkTags (
    val title: String,
    val description: String,
    val image: String,
    val url: String,
    val type: String = "website"
)

data class TwitterCard (
    val card: String = "summary_large_image",
    val site: String = "@casaconalma",
    val creator: String = "@casaconalma",
    val title: String,
    val description: String,
    val image: String
)