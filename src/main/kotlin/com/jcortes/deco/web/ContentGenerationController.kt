package com.jcortes.deco.web

import com.jcortes.deco.content.ContentGenerationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${app.base-path}/content")
class ContentGenerationController(
    private val contentGenerationService: ContentGenerationService
) {

    @GetMapping("/generate")
    fun generateContent() {
        contentGenerationService.generateContent()
    }
}