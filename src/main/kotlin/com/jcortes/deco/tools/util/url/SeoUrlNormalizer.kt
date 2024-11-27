package com.jcortes.deco.tools.util.url

object SeoUrlNormalizer {

    fun normalize(url: String): String {
        return url.lowercase()
            .replace(Regex("[áàäâ]"), "a")
            .replace(Regex("[éèëê]"), "e")
            .replace(Regex("[íìïî]"), "i")
            .replace(Regex("[óòöô]"), "o")
            .replace(Regex("[úùüû]"), "u")
            .replace("ñ", "n")
            .replace(Regex("[^a-z0-9/]"), "-")  // Permite letras, números y "/"
            .replace(Regex("-+"), "-")          // Reemplaza múltiples guiones por uno solo
            .trim('-')                          // Elimina guiones al inicio y final
    }
}