package com.jcortes.deco.article

import com.jcortes.deco.article.model.Article
import com.jcortes.deco.article.model.SearchArticleRequest

interface ArticleRepository {

    fun get(id: Long): Article?
    fun getBySeoUrl(seoUrl: String): Article?
    fun list(ids: List<Long>): List<Article>
    fun search(request: SearchArticleRequest): List<Article>
    fun iterate(): Iterator<Article>
    fun save(article: Article)
}