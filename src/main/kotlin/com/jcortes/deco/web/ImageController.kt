package com.jcortes.deco.web

import com.jcortes.deco.client.UnsplashClient
import com.jcortes.deco.content.ImageService
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

    @GetMapping("/unsplash/download")
    fun downloadImages() {
        val downloadedImages = AtomicInteger(0)
        val parcialDownloadedImages = AtomicInteger(0)
        val processedSourceIds = imageService.processedSourceIds().map { it.substringAfter("::") }.toMutableList()
        val tags = listOf(
            "interior design", "decoration", "decor", "living room", "shower", "scandinavian", "bedroom", "dining room",
            "sofa", "chair", "table", "bathroom", "interior", "home", "home decor", "furniture", "house", "design",
            "architecture", "room", "indoors", "desk", "apartment", "apartment interior", "exterior", "real estate",
            "garden", "pool", "terrace"
        )
        unsplashClient.downloadByTags(tags) { images ->

            val filteredImages = images.items.filter { it.sourceId !in processedSourceIds }
            imageService.describeAndSave(filteredImages)
            processedSourceIds.addAll(filteredImages.map { it.sourceId })

            downloadedImages.addAndGet(filteredImages.size)
            parcialDownloadedImages.addAndGet(filteredImages.size)
            if(parcialDownloadedImages.get() > 100) {
                println("Downloaded ${downloadedImages.get()} images")
                parcialDownloadedImages.set(0)
            }
        }
    }
}