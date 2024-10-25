package com.jcortes.deco.web

import com.jcortes.deco.content.ArticleService
import com.jcortes.deco.content.model.Article
import com.jcortes.deco.content.model.SiteCategory
import com.jcortes.deco.web.model.ResourceItem
import com.jcortes.deco.web.model.Seo
import com.jcortes.deco.web.model.SocialNetworkTags
import com.jcortes.deco.web.model.TwitterCard
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import kotlin.math.abs
import kotlin.random.Random

@Controller
@RequestMapping("\${app.base-path}")
class HomeController(
    private val articleService: ArticleService
) {


    @GetMapping("/", "")
    fun home(model: Model): String {
        model.addAttribute("detail", homeData())
        return "public/home"
    }

    private fun homeData(): HomeData {
        val categoriesOrder = listOf(
            SiteCategory.LIVING_AND_DINING_ROOMS,
            SiteCategory.DECORATION,
            SiteCategory.KITCHENS,
            SiteCategory.BATHROOMS,
            SiteCategory.BEDROOMS,
            SiteCategory.OUTDOORS_AND_GARDENS,
        )
        val articles = articleService.getTrendingGroupedByCategory(categoriesOrder)

        return HomeData(
            title = "Casa con Alma: Diseña espacios con alma que cuentan historias",
            seo = seo(emptyList()),
            sections = articles.entries.map { HomeSection.of(it) },
            featuredArticle = articles.firstEntry().value[abs(Random.nextInt(articles.firstEntry().value.size))],
            trendingArticles = articles.values.flatten().sortedByDescending { 0.5 * it.updateInstant.toEpochMilli() + 0.5 * abs(Random.nextInt(1_000)) }.take(4)
        )
    }

    private fun seo(articles: List<Article>): Seo {
        val socialNetworkTags = SocialNetworkTags(
            title = "Casa con Alma: Diseña espacios con alma que cuentan historias",
            description = "Explora nuestra selección de artículos de decoración y descubre las últimas tendencias, ideas inspiradoras y consejos para transformar tu hogar con estilo.",
            image = "social-network-image-not-found",
            url = "/"
        )
        val twitterCard = TwitterCard(
            title = "Casa con Alma: Diseña espacios con alma que cuentan historias",
            description = "Explora nuestra selección de artículos de decoración y descubre las últimas tendencias, ideas inspiradoras y consejos para transformar tu hogar con estilo.",
            image = "social-network-image-not-found"
        )
        return Seo(
            description = "Explora nuestra selección de artículos de decoración y descubre las últimas tendencias, ideas inspiradoras y consejos para transformar tu hogar con estilo.",
            keywords = articles.flatMap { it.tags ?: emptyList() }.toSet().joinToString(", "),
            socialNetworkTags = socialNetworkTags,
            twitterCard = twitterCard,
            canonicalUrl = "/"
        )
    }

    inner class HomeData(
        val categories: List<ResourceItem> = SiteCategory.entries.map { ResourceItem(it.label, it.seoUrl) },
        val title: String,
        val seo: Seo,
        val sections: List<HomeSection>,
        val featuredArticle: Article,
        val trendingArticles: List<Article>,
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
