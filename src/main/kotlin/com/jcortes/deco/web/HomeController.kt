package com.jcortes.deco.web

import com.jcortes.deco.article.ArticleService
import com.jcortes.deco.article.model.Article
import com.jcortes.deco.article.model.SiteCategory
import com.jcortes.deco.tools.util.url.UrlBuilder
import com.jcortes.deco.web.model.ResourceItem
import com.jcortes.deco.web.model.Seo
import com.jcortes.deco.web.model.SocialNetworkTags
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import kotlin.math.abs
import kotlin.random.Random

@Controller
@RequestMapping
class HomeController(
    private val articleService: ArticleService,
    private val urlBuilder: UrlBuilder
) {


    @GetMapping("/", "", "home")
    fun home(model: Model): String {
        model.addAttribute("detail", homeData())
        return "public/home"
    }

    private fun homeData(): HomeData {
        val categoriesOrder = listOf(
            SiteCategory.DECORATION,
            SiteCategory.LIVING_AND_DINING_ROOMS,
            SiteCategory.KITCHENS,
            SiteCategory.BEDROOMS,
            SiteCategory.OUTDOORS_AND_GARDENS,
            SiteCategory.BATHROOMS,
            SiteCategory.SEASONAL_DECORATION,
//            SiteCategory.WELL_BEING
        )
        val articles = articleService.getTrendingGroupedByCategory(categoriesOrder)
        val featuredArticles = articles.firstEntry().value[abs(Random.nextInt(articles.firstEntry().value.size))]
        val trendingArticles = articles.values.flatten().sortedByDescending { 0.5 * it.updateInstant.toEpochMilli() + 0.5 * abs(Random.nextInt(1_000)) }.take(4)
        val interestingArticles = articles.values.asSequence().flatten().filter { a -> !trendingArticles.map { it.id }.contains(a.id) }
            .sortedByDescending { 0.2 * it.updateInstant.toEpochMilli() + 0.8 * abs(Random.nextInt(1_000)) }.drop(4).take(4)
            .toList()

        return HomeData(
            title = "Casa con Alma: Diseña espacios con alma que cuentan historias",
            seo = seo(articles.values.flatten()),
            sections = articles.entries.map { HomeSection.of(it) },
            featuredArticle = featuredArticles,
            trendingArticles = trendingArticles,
            interestingArticles = interestingArticles
        )
    }

    private fun seo(articles: List<Article>): Seo {
        val socialNetworkTags = SocialNetworkTags(
            title = "Casa con Alma: Diseña espacios con alma que cuentan historias",
            description = "Explora nuestra selección de artículos de decoración y descubre las últimas tendencias, ideas inspiradoras y consejos para transformar tu hogar con estilo.",
            image = articles.firstOrNull()?.images?.firstOrNull()?.seoUrl?.let { urlBuilder.imageUrl(it) } ?: "social-network-image-not-found",
            url = urlBuilder.contentUrl()
        )
        return Seo(
            description = "Explora nuestra selección de artículos de decoración y descubre las últimas tendencias, ideas inspiradoras y consejos para transformar tu hogar con estilo.",
            keywords = articles.flatMap { it.tags ?: emptyList() }.map { it.label }.toSet().joinToString(", "),
            socialNetworkTags = socialNetworkTags,
            canonicalUrl = urlBuilder.contentUrl()
        )
    }

    inner class HomeData(
        val categories: List<ResourceItem> = SiteCategory.entries.map { ResourceItem(it.label, it.seoUrl) },
        val title: String,
        val seo: Seo,
        val sections: List<HomeSection>,
        val featuredArticle: Article,
        val trendingArticles: List<Article>,
        val interestingArticles: List<Article>
    )

    data class HomeSection(
        val category: SiteCategory,
        val mainArticle: Article,
        val articles: List<Article>
    ) {
        companion object {
            fun of(entry: Map.Entry<SiteCategory, List<Article>>): HomeSection {
                return HomeSection(entry.key, entry.value.first(), entry.value.drop(1))
            }
        }
    }
}
