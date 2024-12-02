package com.jcortes.deco.web.model

data class SocialNetworkTags (
    val title: String,
    val description: String,
    val image: String,
    val url: String,
    val type: String = "website"
)