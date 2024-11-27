package com.jcortes.deco.tools.console.entrypoint

import com.jcortes.deco.article.ArticleService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/console")
class ConsoleController(
    private val articleService: ArticleService
) {

    @GetMapping("/images/preview")
    fun previewImages(model: Model): String {
        return "search-images"
    }

    @GetMapping("/scraped-documents/preview")
    fun previewDocuments(model: Model): String {
        return "search-scraped-documents"
    }

    @GetMapping("/articles/preview")
    fun previewArticles(model: Model): String {
        return "search-articles"
    }
}
