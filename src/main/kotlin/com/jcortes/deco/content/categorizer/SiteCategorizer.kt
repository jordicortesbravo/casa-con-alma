package com.jcortes.deco.content.categorizer

import com.jcortes.deco.content.model.SiteCategory

object SiteCategorizer {

    fun categorize(content: String): List<SiteCategory>? {
        return keywordMap.entries
            .filter { (_, keywords) -> keywords.any { keyword -> content.contains(keyword, ignoreCase = true) } }
            .map { (category, _) -> category }
            .takeIf { it.isNotEmpty() }
    }

    private val keywordMap: Map<SiteCategory, List<String>> = mapOf(
        SiteCategory.LIVING_AND_DINING_ROOMS to listOf(
            "alfombra", "tapete", "tapiz", "moqueta", "esterilla", "estera",
            "comedor", "mesa", "silla", "salon", "mantel", "vajilla", "cubiertos", "centro de mesa", "terraza",
            "decoración", "diseño", "interior", "adornos", "decora", "piso", "casa", "cuadros", "velas", "figuras"
        ),
        SiteCategory.KITCHENS to listOf(
            "almacenamiento", "cocina", "despensa", "organizadores", "estantes", "contenedores", "tarros", "botes",
            "cocina", "comedor", "receta", "utensilios", "chef", "cocinar", "alimentos", "ingredientes", "cubiertos", "vaso",
            "microondas", "horno", "licuadora", "batidora", "tostadora", "freidora", "cafetera", "lavavajillas", "refrigerador", "congelador",
            "olla de cocción lenta", "olla a presión", "robot de cocina", "extractor de jugos", "batidora de mano", "grill", "sándwichera",
            "máquina de pan", "vaporizador de alimentos", "espumador de leche", "procesador de alimentos", "hornillo", "plancha eléctrica",
            "rallador eléctrico", "picadora de carne", "molino de café", "secador de frutas", "heladera", "máquina para hacer pasta", "arrocera",
            "hervidor eléctrico", "molinillo de especias", "crepera", "fondue eléctrica", "freidora de aire", "deshidratador de alimentos",
            "moldeador de gofres", "horno tostador", "termo de cocina", "sandwichera", "gofrera", "cocina de inducción", "planchadora de tortillas", "electrodoméstico"
        ),
        SiteCategory.BEDROOMS to listOf(
            "cama", "colchón", "futón", "somier", "sábana", "almohada", "dormitorio", "dormir", "edredón", "mantas", "funda",
            "ropa de cama", "sábanas", "cobija", "almohada", "funda", "edredón", "colcha", "protector de colchón"
        ),
        SiteCategory.BATHROOMS to listOf(
            "baño", "ducha", "lavabo", "inodoro", "bañera", "toalla", "jabonera", "espejo", "wc"
        ),
        SiteCategory.OUTDOORS_AND_GARDENS to listOf(
            "jardín", "exterior", "buganvilla", "plantas", "césped", "hortensia", "peonía", "planta", "plantar", "cactus", "flor", "manguera", "riego", "terraza", "patio",
            "reparación", "mejora", "hogar", "servicios", "herramientas", "bricolaje", "taller", "arreglar", "mantenimiento"
        ),
        SiteCategory.SEASONAL_DECORATION to listOf(
            "navidad", "árbol de navidad", "adornos navideños", "luces navideñas", "decoración navideña",
            "guirnaldas", "nacimiento", "belenes", "figuras navideñas", "regalos", "villancicos",
            "santa claus", "papá noel", "renos", "muérdago", "corona de adviento", "calcetines navideños",
            "turrón", "mazapán", "gorro de navidad", "estrella navideña", "vela navideña",
            "trineo", "duendes", "nieve", "postales navideñas", "año nuevo", "noche buena", "noche vieja", "reyes magos", "san esteban",
            "halloween", "calabaza", "truco o trato", "disfraz", "fantasma", "murciélago", "vampiro", "bruja", "dulces", "fiesta de halloween",
            "pascua", "huevo de pascua", "conejo de pascua", "semana santa", "procesión", "domingo de ramos", "viernes santo", "domingo de resurrección",
            "carnaval", "disfraz", "mardi gras", "carroza", "desfile", "fiesta de carnaval",
            "día de los reyes magos", "cabalgata de reyes", "regalos de reyes", "roscón de reyes",
            "feria", "fiesta", "romería", "verbenas", "festival", "bailes tradicionales", "trajes típicos", "comida típica"
        )
    )
}


