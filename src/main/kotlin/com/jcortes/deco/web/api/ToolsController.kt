package com.jcortes.deco.web.api

import com.jcortes.deco.content.SitemapService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tools")
class ToolsController(
    private val sitemapService: SitemapService
) {

    @GetMapping("/generate-sitemap")
    fun generateSitemap() {
        sitemapService.generateSitemap()
    }
}