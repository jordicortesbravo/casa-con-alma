package com.jcortes.deco.tools.publisher.entrypoint

import com.jcortes.deco.content.ArticleService
import com.jcortes.deco.content.ImageService
import com.jcortes.deco.tools.publisher.PublisherService
import com.jcortes.deco.tools.publisher.SitemapService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tools")
class ToolsController(
    private val sitemapService: SitemapService,
    private val publisherService: PublisherService,
    private val articleService: ArticleService,
    private val imageService: ImageService
) {

    @GetMapping("/publish-content")
    fun publishContent() {
        publisherService.publishContent()
    }
}