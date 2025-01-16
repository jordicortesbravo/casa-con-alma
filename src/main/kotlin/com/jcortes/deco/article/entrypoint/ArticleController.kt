package com.jcortes.deco.article.entrypoint

import com.jcortes.deco.article.ArticleService
import com.jcortes.deco.article.model.Article
import com.jcortes.deco.article.model.CreateArticleRequest
import com.jcortes.deco.article.model.SiteCategory
import com.jcortes.deco.tools.util.paging.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/articles")
class ArticleController(
    private val articleService: ArticleService
) {

    @GetMapping("change-category")
    fun changeCategory(@RequestParam articleId: Long, @RequestParam newCategory: SiteCategory) {
        articleService.setCategory(articleId, newCategory)
    }

    @GetMapping("change-title")
    fun changeTitle(@RequestParam articleId: Long, @RequestParam newTitle: String) {
        articleService.setTitle(articleId, newTitle)
    }

    @GetMapping("/enrich")
    fun enrich() {
        articleService.enrich()
    }

    @GetMapping("/regenerate")
    fun regenerate(@RequestParam articleId: Long) {
        articleService.regenerate(articleId)
    }

    @PostMapping
    fun createFromScratch(@RequestBody createArticleRequest: CreateArticleRequest) {
        articleService.fillArticleWithGenerativeAI(createArticleRequest)
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