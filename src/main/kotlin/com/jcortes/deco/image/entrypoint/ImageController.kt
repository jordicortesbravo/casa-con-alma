package com.jcortes.deco.image.entrypoint

import com.jcortes.deco.image.infrastructure.unsplash.UnsplashClient
import com.jcortes.deco.image.ImageService
import com.jcortes.deco.tools.util.paging.Pageable
import com.jcortes.deco.tools.util.url.UrlBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.File
import java.net.URI
import java.nio.file.Files

@RestController
@RequestMapping("/images", "/static/images")
class ImageController(
    private val unsplashClient: UnsplashClient,
    private val imageService: ImageService,
    private val urlBuilder: UrlBuilder
) {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("{seoUrl}", produces = ["image/webp", "image/jpeg", "image/png"])
    fun getImage(@PathVariable seoUrl: String): ResponseEntity<ByteArray>  {
        val crop = if(seoUrl.endsWith("-150") || seoUrl.endsWith("-480")) {
            "-${seoUrl.substringAfterLast("-")}"
        } else {
            ""
        }
        val image = imageService.getBySeoUrl(seoUrl.replace(".jpg", "").replace("-480", "").replace("-150", ""))
        val bytes = Files.readAllBytes(File(URI(image.internalUri!!.replace(".jpeg", "$crop.webp"))).toPath())
        return ResponseEntity(bytes, HttpStatus.OK)
    }

    @GetMapping("/enrich")
    fun enrichImages() {
        imageService.enrich()
    }

    @GetMapping("/unsplash/download")
    fun downloadImages() {
        val pageable = Pageable(0, 30)
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
            ImageCard(it.id!!, urlBuilder.imageUrl(it.seoUrl)!!, it.description)
        }
    }

    @GetMapping("/related")
    fun related(@RequestParam imageId: Long, @RequestParam page: Int = 1): List<ImageCard> {
        return imageService.related(imageId, Pageable(page, 30)).map {
            ImageCard(it.id!!, urlBuilder.imageUrl(it.seoUrl)!!, it.description)
        }
    }

    data class ImageCard(
        val id: Long,
        val thumbnailUrl: String,
        val description: String?
    )
}