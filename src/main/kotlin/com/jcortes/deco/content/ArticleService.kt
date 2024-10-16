package com.jcortes.deco.content

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jcortes.deco.client.bedrock.BedrockTextClient
import com.jcortes.deco.client.bedrock.BedrockTextInferenceRequest
import com.jcortes.deco.client.bedrock.BedrockTextModel
import com.jcortes.deco.content.model.Article
import com.jcortes.deco.content.model.ArticleSearchRequest
import com.jcortes.deco.content.model.ArticleStatus
import com.jcortes.deco.content.model.Image
import com.jcortes.deco.util.Pageable
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.services.bedrockruntime.model.ThrottlingException

@Service
class ArticleService(
    private val bedrockTextClient: BedrockTextClient,
    private val articleRepository: ArticleRepository,
    private val imageService: ImageService
) {

    @Lazy
    @Autowired
    private lateinit var self: ArticleService

    private val log = LoggerFactory.getLogger(this::class.java)

    private val objectMapper = jacksonObjectMapper()

    fun get(id: Long): Article {
        return articleRepository.get(id) ?: throw NoSuchElementException("Article $id not found")
    }

    fun getTrending(pageable: Pageable): List<Article> {
        val request = ArticleSearchRequest(pageNumber = 1, pageSize = pageable.pageSize)
        return articleRepository.search(request)
    }

    fun generateFromFile() {
        ClassPathResource("article_themes.csv").file.useLines { lines ->
            lines.forEach { line ->
                val (title, subtitle) = line.split(";")
                val article = Article().apply {
                    this.title = title
                    this.subtitle = subtitle
                }
                self.save(article)
            }
        }
    }

    fun enrich() {
        val articles = articleRepository.iterate().asSequence().toList()
        articles.parallelStream().forEach { article ->
            try {
//                article.siteCategories = bedrockTextClient.invokeTextModel(model = BedrockTextModel.CLAUDE_INSTANT, userPrompt = article.title, systemPrompt = CATEGORY_CLASSIFIER_PROMPT) {
//                    it?.let { c -> listOf(SiteCategory.valueOf(c)) } ?: emptyList()
//                }
                generateContent(article)
//                article.embedding = bedrockTextClient.invokeEmbeddingModel(userPrompt = article.content!!)
                self.save(article)
                log.info("Article ${article.id} enriched")
            } catch (te: ThrottlingException) {
                Thread.sleep(20_000)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Transactional
    fun save(article: Article) {
        articleRepository.save(article)
    }

    fun search(
        query: String? = null,
        siteCategories: List<String>? = null,
        tags: List<String>? = null,
        pageable: Pageable
    ): List<Article> {
        val embedding = query?.takeUnless { it.isBlank() }?.let { bedrockTextClient.invokeEmbeddingModel(userPrompt = it) }
        val request = ArticleSearchRequest(embedding = embedding, siteCategories = siteCategories, tags = tags, pageSize = pageable.pageSize, pageNumber = pageable.pageNumber)
        return articleRepository.search(request)
    }

    private fun extractKeywords(text: String?): List<String> {
        return text?.split(",")?.map { it.trim() } ?: emptyList()
    }

    private fun generateContent(article: Article) {
        val inferenceRequest = BedrockTextInferenceRequest().apply {
            model = BedrockTextModel.CLAUDE_SONNET_3_5
            userPrompt = article.title!!
            systemPrompt = CONTENT_PROMPT
            temperature = 1.0f
            maxTokens = 4096
        }
        val generatedContent = bedrockTextClient.invokeTextModel(inferenceRequest) { objectMapper.readTree(it) }
        val content = generatedContent?.get("content")?.asText()
        val keywords = generatedContent?.get("keywords")?.map { it.asText() } ?: emptyList()
        val tags = generatedContent?.get("tags")?.map { it.asText() } ?: emptyList()

        article.content = content
        article.keywords = keywords
        article.tags = tags

        generateImages(article)

        article.status = ArticleStatus.READY_TO_PUBLISH
    }

    private fun generateImages(article: Article) {
        val images = mutableListOf<Image>()
        val content = article.content!!
        val imagePrompts = content.split("<img").drop(1).map { it.substringAfter("data-ia-prompt=\"").substringBefore("\"") }
        imagePrompts.forEach { prompt ->
            val image = imageService.generate(prompt)
            images.add(image)
        }
        article.images = images
    }

    companion object {

        private const val KEYWORDS_PROMPT =
            "Describe keywords del artículo. Da prioridad a la zona de la casa (si aplica) y el resto que describan estilo decorativo, mobiliario, materiales y texturas. Solo retorna un listado separado por comas y todo en minúsculas."

        private const val CATEGORY_CLASSIFIER_PROMPT =
            "Eres un clasificador de texto. Deberás clasificar el texto en la categoría que más se adecúe de este listado: LIVING_AND_DINING_ROOMS(Salones y Comedores), DECORATION(Decoración), KITCHENS(Cocinas), BEDROOMS(Dormitorios), BATHROOMS(Baños), OUTDOORS_AND_GARDENS(Exteriores y Jardines), SEASONAL_DECORATION(Decoración Estacional). Retornarás solamente el valor de la enumeración"


        private const val CONTENT_PROMPT = """
            Genera un artículo que casi alcance el máximo de caracteres en la inferencia y cuyo título sea el que se pasa como entrada. El artículo debe constar de:
                1- Tag h1 con el título
                2- Tag h2 con un párrafo de 2 o 3 frases que sea el subtítulo
                3- Contenido:
                    3.1- Agrega los párrafos que consideres y un número de imágenes entre 3 y 10.
                    3.2- Si lo crees necesario, agrega h3 o listas (li, ul)
                    3.3- Añade las negritas con un tag strong que consideres interesantes.
                    3.4- Cada párrafo en un tag p y las imágenes al final del párrafo. Cada imagen debe estar en un tag img y contendrá:
                        3.4.1- Un atributo data-ia-prompt con un prompt en inglés para generar esa imagen con stable disfussion teniendo en cuenta que el estilo de decoración siempre es elegante. A veces rústico moderno, a veces minimalista y a veces escandinavo. Siempre con predominancia en colores beige, blanco, madera, etc y muy elegante. Debe ser un prompt con mucho detalle de objetos, texturas, materiales y estilo de decoración para que la imagen sea lo más detallada posible.
                        3.4.2- Un atributo caption que será un pie de foto que aporte valor al lector y al SEO
                
                El artículo debe estar optimizado para SEO.
                
                Como respuesta siempre generarás un json con esta estructura:
               
                {  
                   "content":"contenido generado y con el html compactado",
                   "keywords": [], //array de keywords para SEO para ser usadas en un tag meta de html
                   "tags": [] //lista de tags coincidentes para el texto dentro de esta lista: decoración de salones, decoración de comedores, muebles de salón, sofás y sillones, mesas de comedor, decoración de interiores, colores para interiores, iluminación en interiores, estilos de decoración, decoración moderna, decoración rústica, decoración minimalista, decoración vintage, textiles para el hogar, cortinas y estores, alfombras y tapices, cojines decorativos, decoración con plantas, decoración con madera, estanterías y librerías, organización en el hogar, espacios pequeños, decoración de cocinas, cocinas abiertas, colores para cocinas, muebles de cocina, encimeras y superficies, distribución de cocinas, iluminación en cocinas, cocinas modernas, cocinas rústicas, dormitorios infantiles, decoración de dormitorios, cabeceros de cama, ropa de cama, iluminación en dormitorios, armarios y vestidores, dormitorios modernos, dormitorios rústicos, dormitorios minimalistas, decoración de baños, colores para baños, baños pequeños, azulejos y cerámica, muebles de baño, accesorios de baño, iluminación en baños, baños modernos, baños rústicos, jardines y terrazas, decoración de exteriores, muebles de exterior, plantas y flores, iluminación exterior, terrazas pequeñas, patios y jardines, pérgolas y toldos, decoración estacional, decoración de Navidad, decoración de primavera, decoración de verano, decoración de otoño, tendencias de decoración, DIY decoración, decoración low cost, reciclaje y decoración, arte y cuadros decorativos, espejos decorativos, papeles pintados, paredes y revestimientos, reformas del hogar, distribución de espacios, diseño de interiores
                } 
        """
    }

}