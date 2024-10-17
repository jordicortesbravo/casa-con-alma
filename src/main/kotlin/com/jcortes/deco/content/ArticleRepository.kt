package com.jcortes.deco.content

import com.jcortes.deco.content.model.Article
import com.jcortes.deco.content.model.ArticleSearchRequest

interface ArticleRepository {

    fun get(id: Long): Article?
    fun getBySeoUrl(seoUrl: String): Article?
    fun list(ids: List<Long>): List<Article>
    fun search(request: ArticleSearchRequest): List<Article>
    fun iterate(): Iterator<Article>
    fun save(article: Article)
}