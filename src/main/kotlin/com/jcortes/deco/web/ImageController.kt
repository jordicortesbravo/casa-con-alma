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
        val pageable = Pageable(134, 30)
        val processedSourceIds = imageService.processedSourceIds().map { it.substringAfter("::") }.toMutableList()
        val tags = listOf(
            /*"interior design", "decoration", "decor",*/ "living room"/*, "living room interior design", "shower", "scandinavian", "bedroom", "bedroom design", "children's room", "dining room", "kitchen", "kitchen design",
            "sofa", "chair", "table", "bathroom", "interior", "home", "home decor", "furniture", "house", "design", "closet",
            "architecture", "room", "indoors", "desk", "apartment", "apartment interior", "exterior", "real estate",
            "garden", "pool", "terrace", "christmas tree", "christmas decor", "autumn decor"*/
        )
        unsplashClient.downloadByTags(tags, pageable) { images ->
            val filteredImages = images.items.filter { it.sourceId !in processedSourceIds }
            imageService.enrich(filteredImages)
            processedSourceIds.addAll(filteredImages.map { it.sourceId })
        }
    }
}