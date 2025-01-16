package com.jcortes.deco.image.infrastructure.bedrock

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.jcortes.deco.image.model.Image
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient
import software.amazon.awssdk.services.bedrockruntime.model.*
import java.io.File
import java.net.URI

@Component
class BedrockImageClient(
    private val client: BedrockRuntimeClient,
    private val objectMapper: ObjectMapper
) {

    fun describe(base64Image: String): String? {
        try {
            val json = invokeClaudeModel(DESCRIPTION_PROMPT, base64Image)
            return json.get("content")?.firstOrNull()?.get("text")?.asText()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun keywordsOf(base64Image: String): List<String>? {
        try {
            val json = invokeClaudeModel(KEYWORDS_PROMPT, base64Image)
            return json.get("content")?.firstOrNull()?.get("text")?.asText()?.split(",")?.map { it.trim() } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun captionOf(base64Image: String): String? {
        try {
            val json = invokeClaudeModel(CAPTION_PROMPT, base64Image)
            return json.get("content")?.firstOrNull()?.get("text")?.asText()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun characteristicsOf(image: Image): JsonNode? {
        try {
            val message = message(image = image, role = ConversationRole.USER)
            val response = converse(CLAUDE_MODEL, listOf(message), CHARACTERISTICS_PROMPT, CHARACTERISTICS_INFERENCE_CONFIG)
            return objectMapper.readTree(response)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun embeddingsOf(imageDescription: String): List<Float>? {
        try {
            val nativeRequestTemplate = """
                  {
                    "texts": ["{{prompt}}"],
                    "input_type": "search_document",
                    "embedding_types": ["float"]
                  }
                
                """
                .trimIndent()
                .replace("{{prompt}}", imageDescription.replace("\n", " ").replace("\"", "\\\""))

            val response = client.invokeModel { request ->
                request.body(SdkBytes.fromUtf8String(nativeRequestTemplate))
                    .modelId(EMBEDDINGS_MODEL)
            }

            val json = objectMapper.readTree(response.body().asUtf8String())
            return json["embeddings"]["float"].first().map { it.asDouble().toFloat() }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun multimodalEmbeddingsOf(imageDescription: String, base64Image: String): List<Float>? {
        try {
            val nativeRequestTemplate = """
                  {
                    "inputText": "{{imageDescription}}",
                    "inputImage": "{{base64Image}}"
                  }
                """
                .trimIndent()
                .replace("{{imageDescription}}", imageDescription.replace("\n", " ").replace("\"", "\\\""))
                .replace("{{base64Image}}", base64Image)

            val response = client.invokeModel { request ->
                request.body(SdkBytes.fromUtf8String(nativeRequestTemplate))
                    .modelId(MULTIMODAL_EMBEDDINGS_MODEL)
            }

            val json = objectMapper.readTree(response.body().asUtf8String())
            return json["embedding"].map { it.asDouble().toFloat() }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun invokeStableDiffusionModel(inferenceRequest: BedrockImageInferenceRequest): String? {
        try {
            val nativeRequestTemplate = """{"prompt": "{{prompt}}", "negative_prompt": "{{negativePrompt}}"}"""
                .replace("{{prompt}}", inferenceRequest.userPrompt)
                .replace("{{negativePrompt}}", inferenceRequest.negativePrompt ?: "")
            val response = client.invokeModel { request ->
                request.body(SdkBytes.fromUtf8String(nativeRequestTemplate))
                    .contentType("application/json")
                    .modelId(inferenceRequest.model.id)
            }

            val json = objectMapper.readTree(response.body().asUtf8String())
            return json["images"]?.firstOrNull()?.asText()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun seoUrlOf(userPrompt: String?): String? {
        return userPrompt?.let {
            try {
                val message = message(text = it, role = ConversationRole.USER)
                val response = converse(CLAUDE_MODEL, listOf(message), SEO_URL_PROMPT, DEFAULT_INFERENCE_CONFIG)
                response
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun invokeClaudeModel(promptString: String, base64Image: String): JsonNode {
        val nativeRequest = CLAUDE_TEMPLATE
            .trimIndent()
            .replace("{{prompt}}", promptString)
            .replace("{{base64Image}}", base64Image)

        val response = client.invokeModel { request ->
            request.body(SdkBytes.fromUtf8String(nativeRequest))
                .modelId(CLAUDE_MODEL)
        }

        return objectMapper.readTree(response.body().asUtf8String())
    }

    private fun converse(model: String, context: List<Message>, systemPrompt: String, config: InferenceConfiguration): String {
        val response = client.converse { request: ConverseRequest.Builder ->
            request.messages(context)
                .system(SystemContentBlock.builder().text(systemPrompt).build())
                .modelId(model)
                .inferenceConfig(config)
        }
        return response.output().message().content().last().text()
    }

    private fun message(text: String? = null, image: Image? = null, role: ConversationRole): Message {
        val imageBytes = image?.internalUri?.let { File(URI(it)).readBytes() }
        return Message.builder()
            .content(
                ContentBlock.builder()
                    .text(text)
                    .image(imageBytes?.let {
                        ImageBlock.builder()
                            .format("jpeg")
                            .source(ImageSource.builder().bytes(SdkBytes.fromByteArray(it)).build())
                            .build()
                    })
                    .build()
            )
            .role(role)
            .build()
    }

    companion object {
        private const val CLAUDE_MODEL = "anthropic.claude-3-haiku-20240307-v1:0"
        private const val EMBEDDINGS_MODEL = "cohere.embed-multilingual-v3"
        private const val MULTIMODAL_EMBEDDINGS_MODEL = "amazon.titan-embed-image-v1"

        private const val DESCRIPTION_PROMPT =
            "Debes describir las imágenes pueden ser de decoración, interiorismo, jardinería, muebles, cocinas y baños o cualquier oitro tipo de fotografía relacionado con la decoración. Debes ser muy descriptivo y conciso sobre el tipo de decoración, gama de colores, objetos, muebles, texturas y materiales. El objetivo es describir claramente lo que hay en las fotografías para después poder realizar búsquedas semánticas sobre esas fotografías.Las descripciones en español y no más de 2000 carácteres."
        private const val KEYWORDS_PROMPT =
            "Describe keywords de la imagen. Da prioridad a la zona de la casa (si aplica) y el resto que describan la imagen. Solo retorna un listado separado por comas y todo en minúsculas."

        private const val CAPTION_PROMPT = "Genera un caption en español para la imagen de no más de 10 palabras como si fueras un fotógrafo. Debe ser SEO."


        private val CHARACTERISTICS_INFERENCE_CONFIG = InferenceConfiguration.builder()
            .temperature(0.3f)
            .topP(0.9f)
            .maxTokens(2048)
            .build()

        private val DEFAULT_INFERENCE_CONFIG = InferenceConfiguration.builder()
            .temperature(0.5f)
            .topP(0.9f)
            .maxTokens(2048)
            .build()

        private const val CHARACTERISTICS_PROMPT = """
            Eres un identificador de iluminación, tipo de plano en imágenes, nivel de elegancia, si es interior o exterior, si hay personas o animales, paleta de colores, estilo decorativo, texturas, punto focal y cantidad de objetos principales. Extraerás si la luz es de mañana, tarde, noche. Intensidad de luz y si es natural o artificial. También extraerás la elegancia de la imagen, si es interior o exterior, y si hay personas o animales en la imagen. Además, identificarás los colores dominantes, el estilo decorativo (minimalista, industrial, clásico, moderno), las texturas predominantes (madera, metal, tela), si existe un punto focal en la imagen y el número de objetos principales.Tu respuesta únicamente contendrá un json con esta estructura:
            
            {
                "timeOfDay": {
                    "morning": double (probabilidad de luz de mañana),
                    "afternoon": double (probabilidad de luz de tarde),
                    "night": double (probabilidad de luz de noche)
                },
                "lightType": "natural" | "artificial",
                "lightIntensity": double (En el rango de 0 a 1. 0 es totalmente oscuro y 1 es totalmente brillante),
                "elegance": double (Elegancia decorativa en el rango de 0 a 1, donde 0 no es nada elegante y 1 es extremadamente elegante),
                "location": {
                    "interior": double (probabilidad de que la foto sea en interior),
                    "exterior": double (probabilidad de que la foto sea en exterior)
                },
                "photoPerspective": {
                    "generalPlan": double (probabilidad de que la foto sea un plano general),
                    "mediumPlanProbability": double (probabilidad de que la foto sea un plano medio),
                    "closeUpPlanProbability": double (probabilidad de que la foto sea un primer plano),
                    "largeCloseUpPlanProbability": double (probabilidad de que la foto sea un gran primer plano),
                    "detailPlanProbability": double (probabilidad de que la foto sea un plano detalle),
                    "aerialPlanProbability": double (probabilidad de que la foto sea un plano aéreo),
                    "cornerPlanProbability": double (probabilidad de que la foto sea un plano en escuadra)
                },
                "hasPeople": double (probabilidad de que aparezcan personas en la foto),
                "hasAnimals": double (probabilidad de que aparezcan animales en la foto),
                "colorPalette": {
                    "dominantColors": [string] (Colores dominantes en tonalidades en español),
                    "colorfulness": double (Índice de lo colorida que es la imagen, en un rango de 0 a 1)
                },
                "style": {
                    "minimalist": double (probabilidad de que la imagen tenga un estilo minimalista),
                    "industrial": double (probabilidad de que la imagen tenga un estilo industrial),
                    "classic": double (probabilidad de que la imagen tenga un estilo clásico),
                    "modern": double (probabilidad de que la imagen tenga un estilo moderno)
                },
                "texture": {
                    "wood": double (probabilidad de que la textura predominante sea madera),
                    "metal": double (probabilidad de que la textura predominante sea metal),
                    "fabric": double (probabilidad de que la textura predominante sea tela)
                },
                "focalPoint": {
                    "hasFocalPoint": double (probabilidad de que la imagen tenga un punto focal claro),
                    "focalPointObject": string (Objeto que constituye el punto focal en la imagen en español)
                },
                "objectCount": integer (Número de objetos principales en la imagen)
            }

        """

        private const val SEO_URL_PROMPT = "Genera una URL SEO para la imagen de no más de 10 palabras a partir del texto de entrada. La respuesta se limitará a la URL generada."

        private const val CLAUDE_TEMPLATE = """
                {
                    "anthropic_version": "bedrock-2023-05-31",
                    "max_tokens": 2048,
                    "temperature": 0.5,
                    "messages": [
                        {
                             "role": "user",
                             "content": [
                                {
                                    "type": "image",
                                    "source": {
                                        "type": "base64",
                                        "media_type": "image/jpeg",
                                        "data": "{{base64Image}}"
                                    }
                                },
                                {
                                    "type": "text",
                                    "text": "{{prompt}}"
                                }
                             ]
                         }
                    ]
                }
                """
    }

}
