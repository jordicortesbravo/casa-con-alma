package com.jcortes.deco.content.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.Instant

enum class ArticleStatus {
    DRAFT, READY_TO_PUBLISH, PUBLISHED, ARCHIVED
}

@JsonIgnoreProperties("coverImage", "coverImageCaption", "mainCategory", "mainTag")
class Article {

    var id: Long? = null
    var title: String? = null
    var description: String? = "Esta es la descripción del artículo"
    var subtitle: String? = null
    var content: String? = null
    var keywords: List<String>? = null
    var tags: List<String>? = null
    var siteCategories: List<SiteCategory>? = null
    var productCategories: List<String>? = null
    var images: List<Image>? = null
    var status: ArticleStatus = ArticleStatus.DRAFT
    var embedding: List<Float>? = null
    var seoUrl: String? = null
    var createInstant: Instant = Instant.now()
    var updateInstant: Instant = Instant.now()
    var publishInstant: Instant? = null

    val coverImage: String
        get() = images?.firstOrNull()?.seoUrl?.let { "images/$it" } ?: "images/blog/27.jpg"

    val coverImageCaption: String?
        get() = images?.firstOrNull()?.caption

    val mainCategory: SiteCategory?
        get() = siteCategories?.firstOrNull()

    val mainTag: String
        get() = tags?.firstOrNull()?.let { DecorTag.fromLabel(it)?.label } ?: "Aquí va el tag"


}