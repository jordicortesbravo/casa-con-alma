package com.jcortes.deco.article.infrastructure.openai

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class OpenAIClient {

    private val baseUrl = "https://api.openai.com/v1"
    private val client = HttpClient.newBuilder().build()
    private val apiKey = "sk-proj-BpbdSDEeVSFT2TraodURT3BlbkFJaNgbfGw5gEgzkKiRBw30"
    private val organizationId = "org-q5iucBMCyddaDBDKwDRvXjqQ"
    private val projectId = "proj_n7O8yUSGpEQpD3gjYa0TAUEr"

    fun chatCompletion(text: String, llmEngine: LlmEngine = LlmEngine.GPT_4_O_MINI): String {
        val body = """
            {
                "model": "${llmEngine.engineId()}",
                "messages": [
                    {
                        "role": "system",
                        "content": "Actúa como una redactora digital senior sofisticada y con humor de una revista decoración creando el texto centrándote en la calidad del contenido y que esté optimizado para SEO. Reformula los artículos manteniendo la estructura y la idea original, pero cambiando títulos, palabras y frases para evitar problemas de derechos de autor. El número de palabras del texto de salida nunca debe ser inferior al 75% de palabras del texto de entrada y si el artículo contiene listados, reescribe los listados pero mantén la enumeración de los mismos y si es necesario, agrega o elimina alguno de ellos. Elimina cualquier mención a redes sociales"
                    },
                    {
                        "role": "user",
                        "content": "Reformula el siguiente texto para evitar problemas de derechos de autor:\n\n${cleanHtmlTags(text)}"
                    }
                ],
                "max_tokens": 4000
            }
        """.trimIndent()
        val request = requestBuilder("chat/completions")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    private fun requestBuilder(url: String): HttpRequest.Builder {
        return HttpRequest.newBuilder()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer $apiKey")
//            .header("OpenAI-Organization", organizationId)
//            .header("OpenAI-Project", projectId)
            .uri(URI.create("$baseUrl/$url"))
    }

    private fun cleanHtmlTags(htmlText: String): String {
        // Método para limpiar etiquetas HTML si es necesario
        return escapeForJson(htmlText.replace(Regex("<[^>]*>"), ""))
    }

    private fun escapeForJson(input: String): String {
        return input
            .replace("\\", "\\\\") // Escapar las barras invertidas
            .replace("\"", "\\\"")  // Escapar las comillas dobles
            .replace("\n", "\\n")   // Escapar los saltos de línea
            .replace("\r", "\\r")   // Escapar los retornos de carro (por si acaso)
    }
}

enum class LlmEngine {
    DA_VINCI_003,
    GPT_3_5_TURBO,
    GPT_4_O,
    GPT_4_O_MINI;

    fun engineId() = when (this) {
        DA_VINCI_003 -> "text-davinci-003"
        GPT_3_5_TURBO -> "gpt-3.5-turbo-0125"
        GPT_4_O -> "gpt-4o"
        GPT_4_O_MINI -> "gpt-4o-mini"
    }
}