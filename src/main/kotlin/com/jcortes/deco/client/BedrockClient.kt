package com.jcortes.deco.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient

@Component
class BedrockClient(
    private val client: BedrockRuntimeClient,
    private val objectMapper: ObjectMapper
) {

    fun describe(base64Image: String): String? {
        val json = invokeClaudeModel(DESCRIPTION_PROMPT, base64Image)
        return json.get("content")?.firstOrNull()?.get("text")?.asText()
    }

    fun keywordsOf(base64Image: String): List<String> {
        val json = invokeClaudeModel(KEYWORDS_PROMPT, base64Image)
        return json.get("content")?.firstOrNull()?.get("text")?.asText()?.split(",")?.map { it.trim() } ?: emptyList()
    }

    fun captionOf(base64Image: String): String? {
        val json = invokeClaudeModel(CAPTION_PROMPT, base64Image)
        return json.get("content")?.firstOrNull()?.get("text")?.asText()
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


    companion object {
        private const val CLAUDE_MODEL = "anthropic.claude-3-haiku-20240307-v1:0"
        private const val EMBEDDINGS_MODEL = "cohere.embed-multilingual-v3"

        private const val DESCRIPTION_PROMPT =
            "Debes describir las imágenes pueden ser de decoración, interiorismo, jardinería, muebles, cocinas y baños o cualquier oitro tipo de fotografía relacionado con la decoración. Debes ser muy descriptivo y conciso sobre el tipo de decoración, gama de colores, objetos, muebles, texturas y materiales. El objetivo es describir claramente lo que hay en las fotografías para después poder realizar búsquedas semánticas sobre esas fotografías.Las descripciones en español y no más de 2000 carácteres."
        private const val KEYWORDS_PROMPT =
            "Describe keywords de la imagen. Da prioridad a la zona de la casa (si aplica) y el resto que describan la imagen. Solo retorna un listado separado por comas y todo en minúsculas."

        private const val CAPTION_PROMPT = "Genera un caption en español para la imagen de no más de 10 palabras como si fueras un fotógrafo. Debe ser SEO."

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
