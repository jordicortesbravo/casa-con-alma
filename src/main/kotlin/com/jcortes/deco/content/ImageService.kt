package com.jcortes.deco.content

import com.jcortes.deco.client.bedrock.BedrockImageClient
import com.jcortes.deco.client.bedrock.BedrockImageInferenceRequest
import com.jcortes.deco.client.bedrock.BedrockImageModel
import com.jcortes.deco.content.model.Image
import com.jcortes.deco.content.model.ImageSearchRequest
import com.jcortes.deco.util.paging.Pageable
import com.jcortes.deco.util.url.SeoUrlNormalizer
import io.github.mojtabaJ.cwebp.WebpConverter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import kotlin.math.abs
import kotlin.random.Random

@Service
class ImageService(
    private val imageRepository: ImageRepository,
    private val bedrockImageClient: BedrockImageClient
) {

    private val client = HttpClient.newBuilder().build()

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    private val baseImageDir = File("/Users/jcortes/workspace/personal/crawler/images/ia-generated")

    fun get(imageId: Long): Image {
        return imageRepository.get(imageId) ?: throw NoSuchElementException("Image $imageId not found")
    }

    fun getBySeoUrl(seoUrl: String): Image {
        return imageRepository.getBySeoUrl(seoUrl) ?: throw NoSuchElementException("Image $seoUrl not found")
    }

    fun list(): List<Image> {
        return imageRepository.iterate().asSequence().toList()
    }

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
        val embeddings = image.id?.let { imageRepository.getEmbedding(it, EmbeddingType.TEXT) } ?: description?.let { bedrockImageClient.embeddingsOf(it) }
        val multiModalEmbeddings = image.id?.let { imageRepository.getEmbedding(it, EmbeddingType.MULTI_MODAL) } ?: description?.let { bedrockImageClient.multimodalEmbeddingsOf(it, base64Image) }
        val caption = image.caption ?: bedrockImageClient.captionOf(base64Image)
        val characteristics = image.characteristics ?: bedrockImageClient.characteristicsOf(image)
        val seoUrl = (image.seoUrl ?: bedrockImageClient.seoUrlOf(caption ?: description))?.let { SeoUrlNormalizer.normalize(it) }

        image.keywords = keywords ?: emptyList()
        image.description = description
        image.embedding = embeddings
        image.multimodalEmbedding = multiModalEmbeddings
        image.caption = caption
        image.characteristics = characteristics
        image.seoUrl = seoUrl

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
                sourceUrl = imageUrl
                hasRights = true
            }
            val tempFile = File.createTempFile("image", ".jpg")
            tempFile.writeBytes(imageBytes)
            image.internalUri = tempFile.toURI().toString()

            return image
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    fun generate(prompt: String, imageModel: BedrockImageModel?): Image {
        val request = BedrockImageInferenceRequest().apply {
            model = imageModel ?: BedrockImageModel.STABLE_IMAGE_ULTRA
            userPrompt = prompt
        }

        val base64Image = bedrockImageClient.invokeStableDiffusionModel(request)
        val imageId = RANDOM.nextLong(1, Long.MAX_VALUE)
        val image = Image().apply {
            sourceId = "deco-crawler::$imageId"
            hasRights = false
            iaGenerated = true
            sourceUrl = "images/$imageId"
        }
        base64Image?.let { saveImageToDisk(image, it) }
        enrich(image)
        log.info("Generated image: ${image.internalUri.toString().replace("file:", "file://")}")
        return image
    }

    @Transactional
    fun save(image: Image) {
        imageRepository.save(image)
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

    private fun saveImageToDisk(image: Image, base64Image: String) {
        baseImageDir.mkdirs()
        val fileName = "${image.sourceId.substringAfter("::")}.jpeg"
        val dir = File(baseImageDir, (abs(image.sourceId.hashCode() % 50)).toString().padStart(2, '0'))
        dir.mkdirs()
        val file = File(dir, fileName)
        image.internalUri = file.toURI().toString()
        file.writeBytes(Base64.getDecoder().decode(base64Image))
        WebpConverter.imageFileToWebpFile(file.path, file.path.replace("jpeg", "webp"), 80)
    }

    private fun Image.toBase64(): String {
        val imageBytes = this.internalUri?.let { File(URI(it)).readBytes() } ?: throw IllegalStateException("Image has no internal URI")
        return Base64.getEncoder().encodeToString(imageBytes)
    }

    companion object {
        private val RANDOM = Random(System.currentTimeMillis())
    }
}