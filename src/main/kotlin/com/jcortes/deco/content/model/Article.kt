package com.jcortes.deco.content.model

import java.time.Instant

enum class ArticleStatus {
    DRAFT, READY_TO_PUBLISH, PUBLISHED, ARCHIVED
}

class Article {

    var id: Long? = null
    var title: String? = null
    var subtitle: String? = null
    var content: String? = null
    var keywords: List<String>? = null
    var siteCategories: List<SiteCategory>? = null
    var productCategories: List<String>? = null
    var images: List<Image>? = null
    var status: ArticleStatus = ArticleStatus.DRAFT
    var embedding: List<Float>? = null
    var createInstant: Instant = Instant.now()
    var updateInstant: Instant = Instant.now()
    var publishInstant: Instant? = null
}