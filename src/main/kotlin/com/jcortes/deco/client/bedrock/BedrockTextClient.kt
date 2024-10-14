package com.jcortes.deco.client.bedrock

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient
import software.amazon.awssdk.services.bedrockruntime.model.ThrottlingException

@Component
class BedrockTextClient(
    private val client: BedrockRuntimeClient,
    private val objectMapper: ObjectMapper
) {

    fun <T> invokeTextModel(request: BedrockTextInferenceRequest, responseMapper: (String?) -> T?): T? {
        return responseMapper.invoke(invokeTextModel(request))
    }

    fun invokeTextModel(inferenceRequest: BedrockTextInferenceRequest): String? {
        return try {
            val nativeRequest = CLAUDE_TEMPLATE
                .trimIndent()
                .replace("{{userPrompt}}", inferenceRequest.userPrompt.normalize())
                .replace("{{systemPrompt}}", inferenceRequest.systemPrompt?.normalize() ?: "")

            val response = client.invokeModel { request ->
                request.body(SdkBytes.fromUtf8String(nativeRequest))
                    .modelId(inferenceRequest.model.id)
            }

            val json = objectMapper.readTree(response.body().asUtf8String())
            return json.get("content")?.firstOrNull()?.get("text")?.asText()
        } catch (e: Exception) {
            if (e is ThrottlingException) {
                throw e
            }
            e.printStackTrace()
            null
        }
    }

    fun invokeEmbeddingModel(model: BedrockEmbeddingModel = BedrockEmbeddingModel.COHERE_EMBED_MULTILINGUAL_V3, userPrompt: String): List<Float>? {
        try {
            val nativeRequest = COHERE_TEMPLATE
                .trimIndent()
                .replace("{{userPrompt}}", userPrompt.replace("\n", " ").replace("\"", "\\\""))

            val response = client.invokeModel { request ->
                request.body(SdkBytes.fromUtf8String(nativeRequest))
                    .modelId(model.id)
            }

            val json = objectMapper.readTree(response.body().asUtf8String())
            return json["embeddings"]["float"].first().map { it.asDouble().toFloat() }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun String.normalize(): String {
        return this.replace("\n", " ").replace("\"", "").replace(Regex("<[^>]*>"), "")
    }

    companion object {

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

        private const val COHERE_TEMPLATE = """
            {
                "texts": ["{{userPrompt}}"],
                "input_type": "search_document",
                "embedding_types": ["float"]
            }
        """
    }
}
