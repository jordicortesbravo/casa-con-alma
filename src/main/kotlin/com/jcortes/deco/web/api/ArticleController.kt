package com.jcortes.deco.web.api

import com.jcortes.deco.content.ArticleService
import com.jcortes.deco.content.model.Article
import com.jcortes.deco.util.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/articles")
class ArticleController(
    private val articleService: ArticleService
) {

    @GetMapping("/enrich")
    fun enrich() {
        articleService.enrich()
    }

    @GetMapping("/search")
    fun search(
        @RequestParam query: String? = null,
        @RequestParam("category") category: String,
        @RequestParam sort: String? = null,
        @RequestParam page: Int = 1,
        @RequestParam pageSize: Int = 50
    ): ArticlesResponse {
        val categories = if (category.isBlank()) emptyList() else listOf(category)
        return ArticlesResponse(articleService.search(query = query, siteCategories = categories, pageable = Pageable(page, pageSize)))
    }

    data class ArticlesResponse(val results: List<Article>)
}