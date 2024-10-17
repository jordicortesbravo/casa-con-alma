package com.jcortes.deco.web.api

import com.jcortes.deco.client.UnsplashClient
import com.jcortes.deco.content.ImageService
import com.jcortes.deco.util.Pageable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.File
import java.net.URI
import java.nio.file.Files

@RestController
@RequestMapping("\${app.base-path}/images")
class ImageController(
    private val unsplashClient: UnsplashClient,
    private val imageService: ImageService
) {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("{seoUrl}", produces = [MediaType.IMAGE_JPEG_VALUE])
    fun getImage(@PathVariable seoUrl: String): ResponseEntity<ByteArray>  {
        val image = imageService.getBySeoUrl(seoUrl)
        val bytes = Files.readAllBytes(File(URI(image.internalUri!!)).toPath())
        return ResponseEntity(bytes, HttpStatus.OK)
    }

    @GetMapping("/enrich")
    fun enrichImages() {
        imageService.enrich()
    }

    @GetMapping("/unsplash/download")
    fun downloadImages() {
        val pageable = Pageable(1, 30)
        val processedSourceIds = imageService.processedSourceIds().map { it.substringAfter("::") }.toMutableList()
//        val tags = listOf(
//            "interior design", "decoration", "decor", "living room", "living room interior design", "shower", "scandinavian", "bedroom", "bedroom design", "children's room", "dining room", "kitchen", "kitchen design",
//            "sofa", "chair", "table", "bathroom", "interior", "home", "home decor", "furniture", "house", "design", "closet",
//            "architecture", "room", "indoors", "desk", "apartment", "apartment interior", "exterior", "real estate",
//            "garden", "pool", "terrace", "christmas tree", "christmas decor", "autumn decor"
//        )
        val tags = listOf("home")
        unsplashClient.downloadByTags(tags, pageable) { images ->
            val filteredImages = images.items.filter { it.sourceId !in processedSourceIds }
            imageService.enrich(filteredImages)
            processedSourceIds.addAll(filteredImages.map { it.sourceId })
        }
    }

    @GetMapping("/search")
    fun search(@RequestParam query: String? = null, @RequestParam keywords: List<String> = emptyList(), @RequestParam hasRights: Boolean = false, @RequestParam sort: String? = null, @RequestParam page: Int = 1): List<ImageCard> {
        if (query == null && keywords.isEmpty()) {
            return emptyList()
        }
        return  imageService.search(query, keywords, hasRights, Pageable(page, 30)).map {
            ImageCard(it.id!!, it.sourceUrl.toString(), it.description)
        }
    }

    @GetMapping("/related")
    fun related(@RequestParam imageId: Long, @RequestParam page: Int = 1): List<ImageCard> {
        return imageService.related(imageId, Pageable(page, 30)).map {
            ImageCard(it.id!!, it.sourceUrl.toString(), it.description)
        }
    }

    data class ImageCard(
        val id: Long,
        val thumbnailUrl: String,
        val description: String?
    )
}