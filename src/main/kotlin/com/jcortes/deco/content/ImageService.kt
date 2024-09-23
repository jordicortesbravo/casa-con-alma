package com.jcortes.deco.content

import com.jcortes.deco.client.BedrockClient
import com.jcortes.deco.content.model.Image
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.util.*

@Service
class ImageService(
    private val imageRepository: ImageRepository,
    private val bedrockClient: BedrockClient,
) {

    @Transactional
    fun describeAndSave(images: List<Image>) {
        try {
            images.parallelStream().forEach { image ->
                val base64Image = image.toBase64()

                val keywords = bedrockClient.keywordsOf(base64Image)
                val description = bedrockClient.describe(base64Image)
                val embeddings = description?.let { bedrockClient.embeddingsOf(it) }
                val caption = bedrockClient.captionOf(base64Image)

                image.keywords = image.keywords?.plus(keywords) ?: keywords
                image.description = description
                image.embedding = embeddings
                image.caption = caption

                imageRepository.save(image)
            }
        } catch (e: Exception) {
            e.printStackTrace()
//            throw e
        }
    }

    fun processedSourceIds(): List<String> {
        return imageRepository.listSourceIds()
    }

    private fun Image.toBase64(): String {
        val imageBytes = this.internalUri?.let { File(it).readBytes() } ?: throw IllegalStateException("Image has no internal URI")
        return Base64.getEncoder().encodeToString(imageBytes)
    }
}