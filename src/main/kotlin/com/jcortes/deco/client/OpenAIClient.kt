package com.jcortes.deco.client

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main() {
    val client = OpenAIClient()
    val text = "<img src=data:image/svg+xml,%3Csvg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 900 900\"%3E%3C/svg%3E/>\n<img src=https://content.elmueble.com/medio/2024/07/16/5-banos-con-azulejos-zellige-para-inspirarte_3a68b864_00535976_240716143944_900x900.jpg/>\n<p>5 baños con azulejos zellige para inspirarte</p>\n<p>Aunque todavía nos queden por delante varios meses para que 2024 se acabe y le demos la bienvenida al nuevo año, los expertos en decoración de interiores están ya hablando de algunas de las tendencias más novedosas del 2025 y 2026. Sí, estos dos años todavía suenan muy lejos pero, quien tenga en mente renovar algunas de sus estancias, ir recopilando información de lo que sí se llevará y lo que no, les vendrá muy bien para inspirarse y estar a la última.</p>\n<p>Por ejemplo, si lo que quieres renovar es el cuarto de baño, para darle un toque más moderno y llenarlo de estilo, atenta a esta nueva tendencia que viene pisando fuerte. Ya no se llevarán ni el blanco ni la madera que tanto hemos visto en los últimos años, y van a ser sustituidos por un revestimiento muy concreto llamado zellige. Se trata de un tipo de azulejo muy usado tradicionalmente en las casas rústicas y que ahora nos conquista de nuevo por su aspecto artesano.</p>\n<img src=data:image/svg+xml,%3Csvg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 0 0\"%3E%3C/svg%3E/>\n<img src=https://content.elmueble.com/medio/2024/07/12/retrato-de-la-decoradora-alejandra-munoz-duarte_b14d925a_240712150008_600x600.jpg/>\n<p>Truco de experta</p>\n<h3>¿Qué son los azulejos Zellige?</h3>\n<p>Los azulejos zellige son baldosas de cerámica de origen almorávide que se caracterizan por lucir una estética artesanal. Cada azulejo zellige es único, por eso, pueden presentar pequeñas variaciones en color y forma entre ellos, una sutil imperfección que es la que les otorga toda su belleza distintiva.</p>\n<p>Los azulejos zellige están disponibles en muchas formas y tamaños, siendo los más comunes los cuadrados, los rectángulos y los patrones geométricos. Para que entiendas mejor esta tendencia y encuentres inspiración, aquí te dejamos 5 baños con azulejos zellige muy elegantes y artesanales.</p>\n<img src=data:image/svg+xml,%3Csvg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 0 0\"%3E%3C/svg%3E/>\n<img src=https://content.elmueble.com/medio/2024/07/15/tienes-pensado-cambiar-la-cocina-pronto-sumate-al-color-tendencia-de-los-dos-proximos-anos_8f6c1552_240715141312_600x600.jpg/>\n<p>¿Qué color será?</p>\n<p>1.</p>\n<h3>Combinando dos modelos de azulejos</h3>\n<img src=data:image/svg+xml,%3Csvg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 1333 2000\"%3E%3C/svg%3E/>\n<img src=https://content.elmueble.com/medio/2024/07/16/combinando-dos-modelos-de-azulejos_00000000_00521343_240716141923_1333x2000.jpg/>\n<p>Combinando dos modelos de azulejos</p>\n<p>En este cuarto de baño se ve muy bien el contraste tan elegante que genera la combinación de dos revestimientos diferentes, uno de microcemento liso y otro de azulejos zellige con acabado esmaltado. La unidad la consiguen gracias a que ambos son del mismo tono de beige.</p>\n<p>2.</p>\n<h3>Verdes y en la pared de la ducha</h3>\n<img src=data:image/svg+xml,%3Csvg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 2000 3000\"%3E%3C/svg%3E/>\n<img src=https://content.elmueble.com/medio/2024/07/16/verdes-y-en-la-pared-de-la-ducha_00000000_00521345_240716135926_2000x3000.jpg/>\n<p>Verdes y en la pared de la ducha</p>\n<p>Si tienes ganas de añadir esta nueva tendencia de azulejos zellige en tu baño de manera muy sutil, podrías, por ejemplo, instalarlos en una sola pared de la ducha. Elige un color que le añada personalidad, como el verde, que, a su vez, da sensación de profundidad y tranquilidad.</p>\n<p>3.</p>\n<h3>Formando espigas</h3>\n<img src=data:image/svg+xml,%3Csvg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 2000 2996\"%3E%3C/svg%3E/>\n<img src=https://content.elmueble.com/medio/2024/07/16/formando-espigas_00000000_00523145_240716142505_2000x2996.jpg/>\n<p>Formando espigas</p>\n<p>Otra idea muy moderna, y diferente, es revestir con estos azulejos toda una pared, tanto el espacio destinado al lavabo como el trozo de ducha. Para un extra de originalidad, colócalos en forma de espiga y elígelos en un color llamativo, como el azul mar.</p>\n<p>4.</p>\n<h3>Con las juntas del mismo color que el revestimiento</h3>\n<img src=data:image/svg+xml,%3Csvg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 1335 2000\"%3E%3C/svg%3E/>\n<img src=https://content.elmueble.com/medio/2024/07/16/ducha-con-azulejos-pintados-en-azul-00528810_00000000_45e7a4d9_240716142755_1335x2000.jpg/>\n<p>Con las juntas del mismo color que el revestimiento</p>\n<p>Otra forma de jugar con la estética que proporcionan los azulejos zellige es elegirlos en un color poco usual en los baños, en este caso azul marino muy oscuro, y, para aumentar su potencia visual, las juntas entre las baldosas también son de este azul intenso para enfatizar su presencia.</p>\n<p>5.</p>\n<h3>A media pared</h3>\n<img src=data:image/svg+xml,%3Csvg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 1333 2000\"%3E%3C/svg%3E/>\n<img src=https://content.elmueble.com/medio/2024/07/16/mueble-de-bano-con-encimera-de-cemento-y-balda-en-madera-00508442-f0de967d-1333x2000_00000000_155d9d17_240716143311_1333x2000.jpg/>\n<p>A media pared</p>\n<p>Otra opción muy vistosa es revestir con azulejos la pared de la zona del lavabo solo hasta media altura o algo más, como si fuera una especie de zócalo alto. Esta idea es muy práctica si tienes ganas de darle un look actual a tu baño pero cuentas con poco presupuesto.</p>\n<p>6.</p>\n<h3>Y también en la cocina</h3>\n<img src=data:image/svg+xml,%3Csvg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 1471 2000\"%3E%3C/svg%3E/>\n<img src=https://content.elmueble.com/medio/2024/07/16/cocina-blanca-con-perro-00553732_11996bea_240716144439_1471x2000.jpg/>\n<p>Aunque te hemos hablado de la nueva tendencia de los azulejos zellige en la cocina, atenta porque... ¡también se llevan en las cocinas! Mira qué bien quedan cuadrados y en tonos de beige ocupando media pared de la encimera.</p>\n<p>¡Novedad! ¡Tenemos canal de WhatsApp! Sigue toda la información sobre decoración, orden, limpieza y todo lo relacionado con el hogar en el canal de El Mueble en WhatsApp</p>\n<p></p>\n<p></p>\n"
    val response = client.chatCompletion(text)
    println(response)
}

class OpenAIClient {

    private val baseUrl = "https://api.openai.com/v1"
    private val client = HttpClient.newBuilder().build()
    private val apiKey = "sk-proj-BpbdSDEeVSFT2TraodURT3BlbkFJaNgbfGw5gEgzkKiRBw30"
    private val organizationId = "org-q5iucBMCyddaDBDKwDRvXjqQ"
    private val projectId = "proj_n7O8yUSGpEQpD3gjYa0TAUEr"

    fun chatCompletion(text: String, engine: Engine = Engine.GPT_4_O_MINI): String {
        val body = """
            {
                "model": "${engine.engineId()}",
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
            .header("Authorization",  "Bearer $apiKey")
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

enum class Engine {
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