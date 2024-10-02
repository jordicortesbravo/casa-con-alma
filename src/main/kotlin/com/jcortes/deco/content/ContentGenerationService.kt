package com.jcortes.deco.content

import com.jcortes.deco.util.Pageable
import org.springframework.stereotype.Service

@Service
class ContentGenerationService(
    private val imageService: ImageService,
    private val imageRepository: ImageRepository,
    private val scrapedDocumentService: ScrapedDocumentService
) {

    /**
     * Esta aproximación lo que hace es escoger un artículo scrapeado, buscar imágenes relacionadas y enviar las imágenes y el artículo a un modelo de IA para que genere un artículo nuevo en base
     * al texto y las nuevas imágenes.
     */
    fun generateContent() {
        scrapedDocumentService.search("sala de estar", emptyList(), Pageable(0, 5)).forEach { scrapedDocument ->

            println("---------------------------------------------")
            println("Generating content for document: ${scrapedDocument.url}")
            println("Article title is: ${scrapedDocument.title}")
            println()
            println("Images by embedding are:")
            val imagesByEmbedding = imageRepository.search(scrapedDocument.embedding, EmbeddingType.TEXT, emptyList(), false, Pageable(1, 10))
            imagesByEmbedding.forEach { image ->
                println(image.internalUri.toString().replace("file:/", "file:///"))
            }
            println()
            println("Images by title:")
            val imagesByTitle = imageService.search(scrapedDocument.title, emptyList(), false, Pageable(1, 10))
            imagesByTitle.forEach { image ->
                println(image.internalUri.toString().replace("file:/", "file:///"))
            }
            println("---------------------------------------------")
            println()
        }
    }
}