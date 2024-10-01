package com.jcortes.deco.content.model

class Article {

    var id: Long? = null
    var title: String? = null
    var subtitle: String? = null
    var content: String? = null
    var keywords: List<String>? = null
    var siteCategories: List<SiteCategory>? = null
    var productCategories: List<String>? = null
    var images: List<Image>? = null
}