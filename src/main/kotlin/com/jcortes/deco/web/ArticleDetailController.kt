package com.jcortes.deco.web

import com.jcortes.deco.content.ArticleService
import com.jcortes.deco.content.ImageService
import com.jcortes.deco.content.model.Article
import com.jcortes.deco.content.model.DecorTag
import com.jcortes.deco.content.model.SiteCategory
import com.jcortes.deco.util.paging.Pageable
import com.jcortes.deco.util.url.UrlBuilder
import com.jcortes.deco.web.model.ResourceItem
import com.jcortes.deco.web.model.Seo
import com.jcortes.deco.web.model.SocialNetworkTags
import com.jcortes.deco.web.model.TwitterCard
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping
class ArticleDetailController(
    private val articleService: ArticleService,
    private val imageService: ImageService,
    private val urlBuilder: UrlBuilder
) {

    @GetMapping("/article/{id}")
    fun detail(@PathVariable id: Long, model: Model): String {
        val article = articleService.get(id)
        model.addAttribute("detail", detail(article))
        return "public/article-detail"
    }

    @GetMapping("/{category:decoracion|salones-y-comedores|cocinas|dormitorios|banos|exteriores-y-jardines|decoracion-estacional}/{slug}")
    fun detail(@PathVariable category: String, @PathVariable slug: String, model: Model): String {
        val seoUrl = "$category/$slug"
        val article = articleService.getBySeoUrl(seoUrl)
        article.content = SRC_IMG_REGEX.replace(article.content!!) { matchResult ->
            val srcUrl = matchResult.groupValues[2] // Obtén la URL original en el atributo src
            "${matchResult.groupValues[1]}${urlBuilder.imageUrl(srcUrl.replace("images/", ""))}${matchResult.groupValues[3]}"
        }
        model.addAttribute("detail", detail(article))
        return "public/article-detail"
    }

    private fun detail(article: Article): ArticleDetail {
        val trendingArticles = articleService.getTrending(excludedIds = listOf(article.id!!), pageable = Pageable(0, 4))
        val interestingArticles = articleService.getTrending(excludedIds = (trendingArticles + article).mapNotNull { it.id }, pageable = Pageable(1, 4))
        val relatedArticles = articleService.getTrending(excludedIds = (trendingArticles + interestingArticles).mapNotNull { it.id } + article.id!!, pageable = Pageable(0, 8))
        val tags = article.tags?.take(3) ?: DecorTag.entries.toTypedArray().take(3)

        return ArticleDetail(
            article = article,
            breadcrumbs = breadcrumbs(article),
            relatedArticles = relatedArticles,
            featuredArticle = trendingArticles.first(),
            trendingArticles = trendingArticles.subList(1, trendingArticles.size),
            interestingArticles = interestingArticles,
            tags = tags.map { ResourceItem(it.label, it.seoUrl) },
            seo = seo(article)
        )
    }

    private fun breadcrumbs(article: Article): List<ResourceItem> {
        val category = article.siteCategories?.firstOrNull()
        val breadcrumbs = mutableListOf(ResourceItem("Inicio", ""))
        category?.let { breadcrumbs += ResourceItem(category.label, category.seoUrl) }
        breadcrumbs += ResourceItem(article.title!!, article.seoUrl?.toString() ?: "seo-url-not-found")
        return breadcrumbs
    }

    private fun seo(article: Article): Seo {
        val socialNetworkTags = SocialNetworkTags(
            title = article.title ?: "social-network-title-not-found",
            description = article.description ?: "social-network-description-not-found",
            image = article.images?.firstOrNull()?.url?.toString() ?: "social-network-image-not-found",
            url = article.seoUrl?.toString() ?: "seo-url-not-found"
        )
        val twitterCard = TwitterCard(
            title = article.title ?: "social-network-title-not-found",
            description = article.description ?: "social-network-description-not-found",
            image = article.images?.firstOrNull()?.url?.toString() ?: "social-network-image-not-found",
        )
        return Seo(
            description = article.description ?: "social-network-description-not-found",
            keywords = article.tags?.joinToString(", ") ?: "",
            socialNetworkTags = socialNetworkTags,
            twitterCard = twitterCard,
            canonicalUrl = article.seoUrl?.toString() ?: "seo-url-not-found"
        )
    }

    data class ArticleDetail(
        val categories: List<ResourceItem> = SiteCategory.entries.map { ResourceItem(it.label, it.seoUrl) },
        val article: Article,
        val breadcrumbs: List<ResourceItem>,
        val relatedArticles: List<Article>,
        val featuredArticle: Article,
        val trendingArticles: List<Article>,
        val interestingArticles: List<Article>,
        val tags: List<ResourceItem>,
        val seo: Seo
    )

    companion object {
        val SRC_IMG_REGEX = Regex("""(<img\s+class="content-img"\s+src=")([^"]*)(")""")
    }
}
