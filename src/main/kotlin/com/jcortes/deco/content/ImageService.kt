package com.jcortes.deco.content

import com.jcortes.deco.client.BedrockImageClient
import com.jcortes.deco.content.model.Image
import com.jcortes.deco.util.Pageable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.util.*

@Service
class ImageService(
    private val imageRepository: ImageRepository,
    private val bedrockImageClient: BedrockImageClient,
) {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun enrich(images: List<Image>) {
        try {
            images.parallelStream().forEach { image ->

                val base64Image = image.toBase64()

                val keywords = bedrockImageClient.keywordsOf(base64Image)
                val description = bedrockImageClient.describe(base64Image)
                val embeddings = description?.let { bedrockImageClient.embeddingsOf(it) }
                val caption = bedrockImageClient.captionOf(base64Image)

                image.keywords = image.keywords?.plus(keywords ?: emptyList()) ?: keywords
                image.description = description
                image.embedding = embeddings
                image.caption = caption

                imageRepository.save(image)
                log.info("Enriched and saved image: ${image.sourceUrl}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
//            throw e
        }
    }

    fun search(query: String?, keywords: List<String>, hasRights: Boolean? = false, pageable: Pageable): List<Image> {
        val embedding = query?.takeUnless { it.isBlank() }?.let { bedrockImageClient.embeddingsOf(it) }
        return imageRepository.search(embedding, keywords, hasRights, pageable)
    }

    fun processedSourceIds(): List<String> {
        return imageRepository.listSourceIds()
    }

    private fun Image.toBase64(): String {
        val imageBytes = this.internalUri?.let { File(it).readBytes() } ?: throw IllegalStateException("Image has no internal URI")
        return Base64.getEncoder().encodeToString(imageBytes)
    }
}