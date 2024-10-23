package com.jcortes.deco.web

import com.jcortes.deco.content.ArticleService
import com.jcortes.deco.content.ImageService
import com.jcortes.deco.content.model.Article
import com.jcortes.deco.content.model.DecorTag
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
class TagController(
    private val articleService: ArticleService,
    private val imageService: ImageService,
) {

    @GetMapping("/temas/{tag}")
    fun tag(@PathVariable tag: String, model: Model): String {
        val decorTag = DecorTag.entries.find { it.seoUrl.endsWith(tag) } ?: throw IllegalArgumentException("Invalid tag $tag")

        model.addAttribute("detail", detail(decorTag))
        return "public/tag"
    }

    private fun detail(decorTag: DecorTag): TagDetail {
        val articles = articleService.search(tags = listOf(decorTag.label), pageable = Pageable(0, 8 * 4))

        return TagDetail(
            title = "Artículos de ${decorTag.label}",
            tag = decorTag,
            articles = articles,
            seo = seo(decorTag, articles)
        )
    }

    private fun seo(decorTag: DecorTag, articles: List<Article>): Seo {
        val socialNetworkTags = SocialNetworkTags(
            title = "Artículos de ${decorTag.label}",
            description = "Explora nuestra selección de artículos de ${decorTag.label} y descubre las últimas tendencias, ideas inspiradoras y consejos para transformar tu hogar con estilo.",
            image = "social-network-image-not-found",
            url = decorTag.seoUrl
        )
        val twitterCard = TwitterCard(
            title = "Artículos de ${decorTag.label}",
            description = "Explora nuestra selección de artículos de ${decorTag.label} y descubre las últimas tendencias, ideas inspiradoras y consejos para transformar tu hogar con estilo.",
            image = "social-network-image-not-found"
        )
        return Seo(
            description = decorTag.label,
            keywords = articles.flatMap { it.tags ?: emptyList() }.toSet().joinToString(", "),
            socialNetworkTags = socialNetworkTags,
            twitterCard = twitterCard,
            canonicalUrl = decorTag.seoUrl
        )
    }

    data class TagDetail(
        val categories: List<ResourceItem> = SiteCategory.entries.map { ResourceItem(it.label, it.seoUrl) },
        val title: String,
        val tag: DecorTag,
        val articles: List<Article>,
        val seo: Seo
    )

}
