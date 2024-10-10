package com.jcortes.deco.content

import com.jcortes.deco.content.model.Article
import com.jcortes.deco.content.model.ArticleSearchRequest
import com.jcortes.deco.content.model.ScrapedDocument
import com.jcortes.deco.util.Pageable

interface ArticleRepository {

    fun get(id: Long): Article?
    fun list(ids: List<Long>): List<Article>
    fun search(request: ArticleSearchRequest): List<Article>
    fun iterate(): Iterator<Article>
    fun save(article: Article)
}