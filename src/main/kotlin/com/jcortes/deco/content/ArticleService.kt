package com.jcortes.deco.content

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jcortes.deco.client.bedrock.BedrockTextClient
import com.jcortes.deco.client.bedrock.BedrockTextInferenceRequest
import com.jcortes.deco.client.bedrock.BedrockTextModel
import com.jcortes.deco.content.model.*
import com.jcortes.deco.util.paging.Pageable
import com.jcortes.deco.util.url.SeoUrlNormalizer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.services.bedrockruntime.model.ThrottlingException
import java.util.SortedMap

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

    fun iterate() = articleRepository.iterate()

    fun get(id: Long): Article {
        return articleRepository.get(id) ?: throw NoSuchElementException("Article $id not found")
    }

    fun getBySeoUrl(seoUrl: String): Article {
        return articleRepository.getBySeoUrl(seoUrl) ?: throw NoSuchElementException("Article $seoUrl not found")
    }

    fun getTrending(excludedIds: List<Long>? = null, pageable: Pageable): List<Article> {
        val request = ArticleSearchRequest(excludedIds = excludedIds, status = ArticleStatus.READY_TO_PUBLISH, pageNumber = 0, pageSize = pageable.pageSize)
        return articleRepository.search(request)
    }

    fun getTrendingGroupedByCategory(categoriesOrder: List<SiteCategory>): SortedMap<SiteCategory, List<Article>> {
        return categoriesOrder.associateWith {
            val request = ArticleSearchRequest(siteCategories = listOf(it.name), status = ArticleStatus.READY_TO_PUBLISH, pageNumber = 0, pageSize = 4)
            articleRepository.search(request)
        }.toSortedMap(compareBy { categoriesOrder.indexOf(it) })
    }

    fun listPublishable(): List<Article> {
        return articleRepository.iterate().asSequence().filter { it.status == ArticleStatus.READY_TO_PUBLISH  || it.status == ArticleStatus.PUBLISHED }.toList()
    }

    fun search(
        query: String? = null,
        siteCategories: List<String>? = null,
        tags: List<DecorTag>? = null,
        status: ArticleStatus? = null,
        pageable: Pageable
    ): List<Article> {
        val embedding = query?.takeUnless { it.isBlank() }?.let { bedrockTextClient.invokeEmbeddingModel(userPrompt = it) }
        val request = ArticleSearchRequest(embedding = embedding, siteCategories = siteCategories, tags = tags, status = status, pageSize = pageable.pageSize, pageNumber = pageable.pageNumber)
        return articleRepository.search(request)
    }

    fun enrich() {
//        val articles = articleRepository.iterate().asSequence().toList().sortedBy { it.id }
//        val articles = articleRepository.iterate().asSequence().toList().filter { it.status == ArticleStatus.READY_TO_PUBLISH }.sortedBy { it.id }
//        articles.forEach { article ->
//            try {
//               self.fillArticleWithGenerativeAI(article)
//            } catch (te: ThrottlingException) {
//                Thread.sleep(20_000)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
        val articles = articleRepository.iterate().asSequence().toList().filter { it.status == ArticleStatus.DRAFT }.sortedBy { it.id }.groupBy { it.mainCategory }
        for(i in 0..10) {
            articles.forEach { (category, articles) ->
                try {
                    if(i < articles.size) {
                        val article = articles[i]
                        self.fillArticleWithGenerativeAI(article)
                    }
                } catch (te: ThrottlingException) {
                    Thread.sleep(20_000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun regenerate(articleId: Long) {
        fillArticleWithGenerativeAI(get(articleId))
    }

    @Transactional
    fun fillArticleWithGenerativeAI(article: Article, systemPrompt: String? = null) {
        log.info("Generating article ${article.title}")
        generateContent(article, systemPrompt)
        generateSiteCategories(article)
        generateSEOData(article)
        generateEmbedding(article)
        generateAndAddImages(article)
        article.status = ArticleStatus.READY_TO_PUBLISH

        save(article)
        log.info("Generated article (${article.id}): ${article.title}")
    }

    @Transactional
    fun setCategory(articleId: Long, newCategory: SiteCategory) {
        val article = get(articleId)
        val previousCategoryLabel = article.mainCategory?.seoUrl
        article.siteCategories = listOf(newCategory)
        article.seoUrl = article.seoUrl?.replace("$previousCategoryLabel/", "${newCategory.seoUrl}/")
        save(article)
    }

    @Transactional
    fun save(article: Article) {
        articleRepository.save(article)
    }

    private fun generateContent(article: Article, systemPrompt: String? = null) {
        log.info("Generating content for article ${article.title}")
        val inferenceRequest = BedrockTextInferenceRequest().apply {
            this.model = BedrockTextModel.CLAUDE_SONNET_3_5
            this.userPrompt = article.title!!
            this.systemPrompt = systemPrompt ?: CONTENT_PROMPT
            this.temperature = 1.0f
            this.maxTokens = 4096
        }
        val generatedContent = bedrockTextClient.invokeTextModel(inferenceRequest) { objectMapper.readTree(it) }
        val content = generatedContent?.get("content")?.asText()
        article.content = content
        log.info("Generated content for article ${article.title}")
    }

    private fun generateSEOData(article: Article) {
        log.info("Generating SEO data for article ${article.title}")
        val inferenceRequest = BedrockTextInferenceRequest().apply {
            model = BedrockTextModel.CLAUDE_HAIKU_3
            userPrompt = article.content!!
            systemPrompt = SEO_PROMPT
            temperature = 1.0f
        }
        val json = bedrockTextClient.invokeTextModel(inferenceRequest) { objectMapper.readTree(it) }

        article.description = json?.get("description")?.asText()
        article.seoUrl = json?.get("seoUrl")?.asText()?.let { SeoUrlNormalizer.normalize("${article.mainCategory?.seoUrl}/$it") }
        article.keywords = json?.get("keywords")?.asSequence()?.map { it.asText() }?.toList()
        article.tags = json?.get("tags")?.asSequence()?.mapNotNull { try { DecorTag.valueOf(it.asText())} catch(e: Exception) { null } }?.toList()
        log.info("Generated SEO data for article ${article.title}")
    }

    private fun generateEmbedding(article: Article) {
        log.info("Generating embedding for article ${article.title}")
        article.embedding = bedrockTextClient.invokeEmbeddingModel(userPrompt = article.description!!)
        log.info("Generated embedding for article ${article.title}")
    }

    private fun generateSiteCategories(article: Article) {
        log.info("Generating site categories for article ${article.title}")
        val request = BedrockTextInferenceRequest().apply {
            model = BedrockTextModel.CLAUDE_INSTANT
            userPrompt = article.title!!
            systemPrompt = CATEGORY_CLASSIFIER_PROMPT
        }
        article.siteCategories = article.siteCategories ?: bedrockTextClient.invokeTextModel(request) {
            it?.let { c -> listOf(SiteCategory.valueOf(c)) } ?: emptyList()
        }
        log.info("Generated site categories for article ${article.title}")
    }

    fun generateAndAddImages(article: Article) {
        log.info("Generating images for article ${article.title}")
        val images = mutableListOf<Image>()
        var content = article.content!!
        val imgMatches = IMG_TAG_REGEX.findAll(content).toList()

        imgMatches.forEach { matchResult ->
            val prompt = matchResult.groupValues[1]
            val image = imageService.generate(prompt)
            images.add(image)

            val newImgTag = """<div class="content-img-container"><img class="content-img" src="${image.seoUrl}" alt="${image.caption}"/></div>"""
            content = content.replaceFirst(IMG_TAG_REGEX, newImgTag)
        }
        article.content = content.replace("</img>", "")
        article.images = images
        log.info("Generated images for article ${article.title}")
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
                        3.4.2- No debe incluir ningún atributo más
                
                El artículo debe estar optimizado para SEO.
                
                Como respuesta siempre generarás un json con esta estructura:
               
                {  
                   "content":"contenido generado y con el html compactado"
                } 
        """

        private const val SEO_PROMPT = """
             Debes generar metainformación de un artículo de decoración e interiorismo para que quede bien optimizado para SEO:

            Como respuesta siempre generarás un json con esta estructura:
                           
                {  
                   "description": "Breve resumen del artículo",
                   "seoUrl":"URL SEO friendly a partir del contenido del artículo",
                   "keywords": [], //array de keywords para SEO para ser usadas en un tag meta de html
                   "tags": [] //lista de tags que puedan encajar con el contenido de las disponibles de la enumeración DecorTag. Solo incluye las que tengan una probabilidad de pertenecer a ese tag en más de un 0.75
                }

                enum class DecorTag {
                    LIVING_ROOM_DECOR,
                    DINING_ROOM_DECOR,
                    LIVING_ROOM_FURNITURE,
                    SOFAS_ARMCHAIRS,
                    DINING_TABLES,
                    INTERIOR_DECOR,
                    INTERIOR_COLORS,
                    INTERIOR_LIGHTING,
                    DECOR_STYLES,
                    MODERN_DECOR,
                    RUSTIC_DECOR,
                    MINIMALIST_DECOR,
                    VINTAGE_DECOR,
                    HOME_TEXTILES,
                    CURTAINS_BLINDS,
                    RUGS_TAPESTRIES,
                    DECORATIVE_CUSHIONS,
                    PLANT_DECOR,
                    WOOD_DECOR,
                    SHELVES_BOOKCASES,
                    HOME_ORGANIZATION,
                    SMALL_SPACES,
                    KITCHEN_DECOR,
                    OPEN_KITCHENS,
                    KITCHEN_COLORS,
                    KITCHEN_FURNITURE,
                    COUNTERTOPS_SURFACES,
                    KITCHEN_LAYOUT,
                    KITCHEN_LIGHTING,
                    MODERN_KITCHENS,
                    RUSTIC_KITCHENS,
                    KIDS_BEDROOMS,
                    BEDROOM_DECOR,
                    BED_HEADBOARDS,
                    BEDDING,
                    BEDROOM_LIGHTING,
                    WARDROBES_CLOSET,
                    MODERN_BEDROOMS,
                    RUSTIC_BEDROOMS,
                    MINIMALIST_BEDROOMS,
                    BATHROOM_DECOR,
                    BATHROOM_COLORS,
                    SMALL_BATHROOMS,
                    TILES_CERAMICS,
                    BATHROOM_FURNITURE,
                    BATHROOM_ACCESSORIES,
                    BATHROOM_LIGHTING,
                    MODERN_BATHROOMS,
                    RUSTIC_BATHROOMS,
                    GARDENS_TERRACES,
                    OUTDOOR_DECOR,
                    OUTDOOR_FURNITURE,
                    PLANTS_FLOWERS,
                    OUTDOOR_LIGHTING,
                    SMALL_TERRACES,
                    PATIOS_GARDENS,
                    PERGOLAS_AWNINGS,
                    SEASONAL_DECOR,
                    CHRISTMAS_DECOR,
                    SPRING_DECOR,
                    SUMMER_DECOR,
                    FALL_DECOR,
                    DECOR_TRENDS,
                    DIY_DECOR,
                    LOW_COST_DECOR,
                    RECYCLED_DECOR,
                    DECOR_ART,
                    DECOR_MIRRORS,
                    WALLPAPERS,
                    WALLS_COVERINGS,
                    HOME_RENOVATIONS,
                    SPACE_LAYOUT,
                    INTERIOR_DESIGN;
                }
        """

        val IMG_TAG_REGEX = Regex("<img\\s+[^>]*data-ia-prompt=['\"]([^'\"]+)['\"][^>]*(/?>|></img>)")
    }

}