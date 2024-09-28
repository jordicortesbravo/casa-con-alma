package com.jcortes.deco.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.jcortes.deco.content.model.ScrapedDocument
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient
import software.amazon.awssdk.services.bedrockruntime.model.ThrottlingException

@Component
class BedrockDocumentClient(
    private val client: BedrockRuntimeClient,
    private val objectMapper: ObjectMapper
) {

    fun describe(document: ScrapedDocument): String? {
        return document.content?.let {
            try {
                val json = invokeClaudeModel(it, DESCRIPTION_PROMPT)
                json.get("content")?.firstOrNull()?.get("text")?.asText()
            }catch(e: Exception) {
                if(e is ThrottlingException) {
                    throw e
                }
                e.printStackTrace()
                null
            }
        }
    }

    fun keywordsOf(document: ScrapedDocument): List<String>? {
        return document.content?.let {
            try {
                val json = invokeClaudeModel(it, KEYWORDS_PROMPT)
                json.get("content")?.firstOrNull()?.get("text")?.asText()?.split(",")?.map { it.trim() } ?: emptyList()
            } catch(e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun embeddingsOf(document: ScrapedDocument): List<Float>? {
        if(document.resume == null) {
            return null
        }
        try {
            val nativeRequestTemplate = """
                  {
                    "texts": ["{{prompt}}"],
                    "input_type": "search_document",
                    "embedding_types": ["float"]
                  }

                """
                .trimIndent()
                .replace("{{prompt}}", document.resume!!.replace("\n", " ").replace("\"", "\\\""))

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

    private fun invokeClaudeModel(userPrompt: String, systemPrompt: String): JsonNode {
        val nativeRequest = CLAUDE_TEMPLATE
            .trimIndent()
            .replace("{{userPrompt}}", userPrompt.replace("\n", " ").replace("\"", "").replace(Regex("<[^>]*>"), ""))
            .replace("{{systemPrompt}}", systemPrompt)



        val response = client.invokeModel { request ->
            request.body(SdkBytes.fromUtf8String(nativeRequest))
                .modelId(CLAUDE_MODEL)
        }

        return objectMapper.readTree(response.body().asUtf8String())
    }


    companion object {
//        private const val CLAUDE_MODEL = "anthropic.claude-3-haiku-20240307-v1:0"
        private const val CLAUDE_MODEL = "anthropic.claude-3-5-sonnet-20240620-v1:0"
        private const val EMBEDDINGS_MODEL = "cohere.embed-multilingual-v3"

        private const val DESCRIPTION_PROMPT =
            "Eres un sistema encargado de resumir artículos de un blog de decoración e interiorismo. Tu principal cometido es hacer un resumen claro y conciso de no más de 2048 caracteres para poder generar un embedding para realizar búsquedas semánticas de contenido. Ignorarás por completo los tags html que se pasen en la entrada y te centrarás solamente en el texto. Es importante que detalles qué tipo de artículos se detallan en los artículos para poder relacionar los artículos con imágenes en procesos posteriores."
        private const val KEYWORDS_PROMPT =
            "Describe keywords del artículo. Da prioridad a la zona de la casa (si aplica) y el resto que describan estilo decorativo, mobiliario, materiales y texturas. Solo retorna un listado separado por comas y todo en minúsculas."

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
                                    "type": "text",
                                    "text": "{{userPrompt}}"
                                }
                             ]
                         }
                    ],
                    "system": [
                        {
                            "type": "text",
                            "text": "{{systemPrompt}}"
                        }
                    ]
                }
                """
    }

}
