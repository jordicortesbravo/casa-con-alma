package com.jcortes.deco.content

import com.jcortes.deco.client.bedrock.BedrockImageClient
import com.jcortes.deco.content.model.Image
import com.jcortes.deco.content.model.ImageSearchRequest
import com.jcortes.deco.util.Pageable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import kotlin.random.Random

@Service
class ImageService(
    private val imageRepository: ImageRepository,
    private val bedrockImageClient: BedrockImageClient
) {

    private val client = HttpClient.newBuilder().build()

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun enrich() {
        val images = imageRepository.iterate().asSequence().toList()
        enrich(images)
//        val it = imageRepository.iterate()
//        while (it.hasNext()) {
//            val image = it.next()
//            enrich(image)
//        }
    }

    @Transactional
    fun enrich(images: List<Image>) {
        try {
            images.parallelStream().forEach { image ->
                enrich(image)
            }
        } catch (e: Exception) {
            e.printStackTrace()
//            throw e
        }
    }

    private fun enrich(image: Image) {
        val base64Image = image.toBase64()
        val keywords = image.keywords ?: bedrockImageClient.keywordsOf(base64Image)
        val description = image.description ?: bedrockImageClient.describe(base64Image)
        val embeddings = imageRepository.getEmbedding(image.id!!, EmbeddingType.TEXT) ?: description?.let { bedrockImageClient.embeddingsOf(it) }
        val multiModalEmbeddings = imageRepository.getEmbedding(image.id!!, EmbeddingType.MULTI_MODAL) ?: description?.let { bedrockImageClient.multimodalEmbeddingsOf(it, base64Image) }
        val caption = image.caption ?: bedrockImageClient.captionOf(base64Image)
        val characteristics = image.characteristics ?: bedrockImageClient.characteristicsOf(image)

        image.keywords = keywords ?: emptyList()
        image.description = description
        image.embedding = embeddings
        image.multimodalEmbedding = multiModalEmbeddings
        image.caption = caption
        image.characteristics = characteristics

        imageRepository.save(image)
        log.info("Enriched and saved image: ${image.sourceUrl}")
    }

    fun download(imageUrl: String): Image {
        val request = HttpRequest.newBuilder()
            .uri(URI(imageUrl))
            .GET()
            .build()
        client.send(request, HttpResponse.BodyHandlers.ofByteArray()).body().let { imageBytes ->
            val image = Image().apply {
                sourceId = "el-mueble::${RANDOM.nextLong()}"
                sourceUrl = URI(imageUrl)
                hasRights = true
            }
            val tempFile = File.createTempFile("image", ".jpg")
            tempFile.writeBytes(imageBytes)
            image.internalUri = tempFile.toURI()

            return image
        }
    }

    fun generate(prompt: String): Image {
        TODO ("not implemented yet")
    }

    fun search(query: String?, keywords: List<String>, hasRights: Boolean? = false, pageable: Pageable): List<Image> {
        val embedding = query?.takeUnless { it.isBlank() }?.let { bedrockImageClient.embeddingsOf(it) }
        val request = ImageSearchRequest(
            searchEmbedding = embedding,
            keywords = keywords,
            hasRights = hasRights,
            pageSize = pageable.pageSize,
            pageNumber = pageable.pageNumber
        )
        return imageRepository.search(request)
    }

    fun related(imageId: Long, pageable: Pageable): List<Image> {
        val image = imageRepository.get(imageId) ?: throw IllegalArgumentException("Image not found")
        val request = ImageSearchRequest(
            searchEmbedding = image.multimodalEmbedding,
            embeddingType = EmbeddingType.MULTI_MODAL,
            keywords = image.keywords ?: emptyList(),
            hasRights = false,
            elegance = 0.7f,
            pageSize = pageable.pageSize,
            pageNumber = pageable.pageNumber
        )
        return imageRepository.search(request)
    }

    fun processedSourceIds(): List<String> {
        return imageRepository.listSourceIds()
    }

    private fun Image.toBase64(): String {
        val imageBytes = this.internalUri?.let { File(it).readBytes() } ?: throw IllegalStateException("Image has no internal URI")
        return Base64.getEncoder().encodeToString(imageBytes)
    }

    companion object {
        private val RANDOM = Random(System.currentTimeMillis())
    }
}