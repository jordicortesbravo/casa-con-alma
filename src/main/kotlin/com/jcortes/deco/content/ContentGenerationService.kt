package com.jcortes.deco.content

import com.jcortes.deco.content.model.Image
import com.jcortes.deco.content.model.ImageSearchRequest
import com.jcortes.deco.content.model.ScrapedDocument
import com.jcortes.deco.util.Pageable
import org.springframework.stereotype.Service
import smile.math.distance.EuclideanDistance
import java.util.concurrent.ConcurrentSkipListSet

@Service
class ContentGenerationService(
    private val imageService: ImageService,
    private val imageRepository: ImageRepository,
    private val scrapedDocumentService: ScrapedDocumentService,
    private val articleService: ArticleService
) {

    fun generateContent() {
        articleService.generateFromFile()
    }

//    private val usedImages = mutableSetOf<Image>()
//
//    fun generateContent() {
//        scrapedDocumentService.search("sala de estar", emptyList(), Pageable(0, 5)).forEach { doc ->
//
//            println("---------------------------------------------")
//            println("Generating content for document (${doc.id}): ${doc.url}")
//            println("Article title is: ${doc.title}")
//            println()
//            getBestImages(doc).forEach { (image, bestImage) ->
//                println("Source Image (${image.id}): ${image.sourceUrl}")
//                println("Related image (${bestImage.id}): ${bestImage.sourceUrl}")
//                println()
//                usedImages.add(bestImage)
//            }
//        }
//    }
//
//
//    private fun getBestImages(scrapedDocument: ScrapedDocument): List<Pair<Image, Image>> {
//        val bestImages = mutableSetOf<Pair<Image, Image>>()
//
//        scrapedDocument.images?.toSet()?.parallelStream()?.forEach { image ->
//            val relatedImages = imageService.related(image.id!!, Pageable(1, 200))
//            val bestImage = relatedImages
//                .filter { usedImages.contains(it).not() }
//                .filter { it.multimodalEmbedding != null && it.characteristics != null && it.keywords?.isNotEmpty() == true }
//                .maxByOrNull { relatedImage ->
//                    val characteristics = image.characteristics
//                    val lightIntensity = 0.15 * (characteristics?.get("lightIntensity")?.asDouble() ?: 0.0)
//                    val elegance = 0.25 * (characteristics?.get("elegance")?.asDouble() ?: 0.0)
//                    val colors = 0.1 * (characteristics?.get("colorPalette")?.get("dominantColors")?.intersect((image.characteristics?.get("colorPalette")?.get("dominantColors") ?: emptyList()).toSet())?.size?.toDouble() ?: 0.0)
//                    val scoreKeywords = 0.15 * (relatedImage.keywords?.intersect((image.keywords ?: emptyList()).toSet())?.size?.toDouble() ?: 0.0)
//                    val similarity = 0.35 * EuclideanDistance().d(relatedImage.embedding?.toFloatArray() , image.embedding?.toFloatArray())
//
//                    lightIntensity + elegance + colors + scoreKeywords + similarity
//                }
//            bestImages.add(image to bestImage!!)
//            usedImages.add(bestImage)
//        }
//
//        return bestImages.toList()
//    }
}