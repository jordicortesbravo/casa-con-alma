package com.jcortes.deco.web

import com.jcortes.deco.content.model.SiteCategory
import com.jcortes.deco.web.model.ResourceItem
import com.jcortes.deco.web.model.Seo
import com.jcortes.deco.web.model.SocialNetworkTags
import com.jcortes.deco.web.model.TwitterCard
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/404")
class ErrorController {

    @GetMapping
    fun error404(model: Model): String {
        model.addAttribute("detail", ErrorData())
        return "public/404"
    }

    private fun seo(): Seo {
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
            keywords = "",
            socialNetworkTags = socialNetworkTags,
            twitterCard = twitterCard,
            canonicalUrl = "/"
        )
    }

    inner class ErrorData(
        val categories: List<ResourceItem> = SiteCategory.entries.map { ResourceItem(it.label, it.seoUrl) },
        val seo: Seo = seo(),
        val title: String = "Error 404 - Casa con alma",
    )
}