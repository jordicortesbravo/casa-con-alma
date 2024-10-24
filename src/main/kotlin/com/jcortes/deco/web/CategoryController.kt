package com.jcortes.deco.web

import com.jcortes.deco.content.ArticleService
import com.jcortes.deco.content.ImageService
import com.jcortes.deco.content.model.Article
import com.jcortes.deco.content.model.ArticleStatus
import com.jcortes.deco.content.model.SiteCategory
import com.jcortes.deco.util.Pageable
import com.jcortes.deco.web.ArticleDetailController.*
import com.jcortes.deco.web.model.ResourceItem
import com.jcortes.deco.web.model.Seo
import com.jcortes.deco.web.model.SocialNetworkTags
import com.jcortes.deco.web.model.TwitterCard
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("\${app.base-path}")
class CategoryController(
    private val articleService: ArticleService,
    private val imageService: ImageService
) {

    @GetMapping("/{category:decoracion|salones-y-comedores|cocinas|dormitorios|banos|exteriores-y-jardines|decoracion-estacional}")
    fun category(@PathVariable category: String, model: Model): String {
        val siteCategory = SiteCategory.entries.find { it.seoUrl.contains(category) } ?: throw IllegalArgumentException("Invalid category $category")

        model.addAttribute("detail", detail(siteCategory))
        return "public/category"
    }

    private fun detail(siteCategory: SiteCategory): CategoryDetail {
        val articles = articleService.search(siteCategories = listOf(siteCategory.name), status = ArticleStatus.READY_TO_PUBLISH, pageable = Pageable(0, 8 * 4))

        return CategoryDetail(
            title = "Artículos de ${siteCategory.label}",
            category = siteCategory,
            articles = articles,
            tags = emptyList(),
            seo = seo(siteCategory, articles)
        )
    }

    private fun seo(category: SiteCategory, articles: List<Article>): Seo {
        val socialNetworkTags = SocialNetworkTags(
            title = "Artículos de ${category.label}",
            description = "Explora nuestra selección de artículos de ${category.label} y descubre las últimas tendencias, ideas inspiradoras y consejos para transformar tu hogar con estilo.",
            image = "social-network-image-not-found",
            url = category.seoUrl
        )
        val twitterCard = TwitterCard(
            title = "Artículos de ${category.label}",
            description = "Explora nuestra selección de artículos de ${category.label} y descubre las últimas tendencias, ideas inspiradoras y consejos para transformar tu hogar con estilo.",
            image = "social-network-image-not-found"
        )
        return Seo(
            description = category.label,
            keywords = articles.flatMap { it.tags ?: emptyList() }.toSet().joinToString(", "),
            socialNetworkTags = socialNetworkTags,
            twitterCard = twitterCard,
            canonicalUrl = category.seoUrl
        )
    }

    data class CategoryDetail(
        val categories: List<ResourceItem> = SiteCategory.entries.map { ResourceItem(it.label, it.seoUrl) },
        val title: String,
        val category: SiteCategory,
        val articles: List<Article>,
        val tags: List<ResourceItem>,
        val seo: Seo
    )

}
