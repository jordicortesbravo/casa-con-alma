package com.jcortes.deco.scrapeddocument.model

import com.jcortes.deco.article.model.SiteCategory
import com.jcortes.deco.image.model.Image
import java.net.URI
import java.time.Instant

class ScrapedDocument {
    var id: Long? = null
    var sourceId: String? = null
    lateinit var url: URI
    var title: String? = null
    var subtitle: String? = null
    var content: String? = null
    var resume: String? = null
    var createInstant: Instant = Instant.now()
    var updateInstant: Instant? = Instant.now()
    var keywords: List<String>? = null
    var siteCategories: List<SiteCategory>? = null
    var productCategories: List<String>? = null
    var relatedLinks: List<URI>? = null
    var images: List<Image>? = null
    var embedding: List<Float>? = null
}