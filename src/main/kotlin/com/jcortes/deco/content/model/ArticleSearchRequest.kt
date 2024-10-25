package com.jcortes.deco.content.model

data class ArticleSearchRequest (
    val embedding: List<Float>? = null,
    val keywords: List<String>? = null,
    val tags: List<String> ? = null,
    val siteCategories: List<String> ? = null,
    val status: ArticleStatus? = null,
    val excludedIds: List<Long> ? = null,
    val pageSize: Int = 20,
    val pageNumber: Int = 0
)