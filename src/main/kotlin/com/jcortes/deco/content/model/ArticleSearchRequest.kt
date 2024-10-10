package com.jcortes.deco.content.model

data class ArticleSearchRequest (
    val embedding: List<Float>? = null,
    val keywords: List<String> = emptyList(),
    val siteCategories: List<String> = emptyList(),
    val pageSize: Int = 20,
    val pageNumber: Int = 1,
)