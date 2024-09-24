package com.jcortes.deco.web

import com.jcortes.deco.client.UnsplashClient
import com.jcortes.deco.content.ImageService
import com.jcortes.deco.util.Pageable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicInteger

@RestController
@RequestMapping("\${app.base-path}/images")
class ImageController(
    private val unsplashClient: UnsplashClient,
    private val imageService: ImageService
) {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/unsplash/download")
    fun downloadImages() {
        val pageable = Pageable(140, 30)
        val processedSourceIds = imageService.processedSourceIds().map { it.substringAfter("::") }.toMutableList()
        val tags = listOf(
            "interior design"/*, "decoration", "decor", "living room", "shower", "scandinavian", "bedroom", "dining room",
            "sofa", "chair", "table", "bathroom", "interior", "home", "home decor", "furniture", "house", "design",
            "architecture", "room", "indoors", "desk", "apartment", "apartment interior", "exterior", "real estate",
            "garden", "pool", "terrace", "christmas tree", "halloween"*/
        )
        unsplashClient.downloadByTags(tags, pageable) { images ->
            val filteredImages = images.items.filter { it.sourceId !in processedSourceIds }
            imageService.enrich(filteredImages)
            processedSourceIds.addAll(filteredImages.map { it.sourceId })
        }
    }
}