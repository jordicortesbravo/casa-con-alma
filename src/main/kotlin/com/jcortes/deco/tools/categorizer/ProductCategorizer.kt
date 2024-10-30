package com.jcortes.deco.tools.categorizer

object ProductCategorizer {

    fun categorize(content: String): List<String>? {
        return keywordMap.entries
            .filter { (_, keywords) -> keywords.any { keyword -> content.contains(keyword, ignoreCase = true) } }
            .map { (category, _) -> category }
            .takeIf { it.isNotEmpty() }
    }

    private val keywordMap = mapOf(
        "organizacion-hogar" to listOf("organización", "almacenaje", "orden", "guardar", "estantería", "organizador", "cajas", "baúles"),
        "alfombras" to listOf("alfombra", "tapete", "tapiz", "moqueta", "esterilla", "estera"),
        "almacenamiento-cocina" to listOf("almacenamiento", "cocina", "despensa", "organizadores", "estantes", "contenedores", "tarros", "botes"),
        "artesania" to listOf("artesanía", "costura", "tejer", "manualidades", "bordado", "crochet", "macramé", "diy"),
        "aspiradoras" to listOf("aspiradora", "enceradora", "limpieza", "polvo", "aspirar", "limpiadora", "vacuum", "suction"),
        "bano" to listOf("baño", "ducha", "lavabo", "inodoro", "bañera", "toalla", "jabonera", "espejo", "wc"),
        "cama" to listOf("cama", "colchón", "futón","somier", "sábana", "almohada", "dormitorio", "dormir", "edredón", "mantas", "funda"),
        "ropa-cama" to listOf("ropa de cama", "sábanas", "cobija", "almohada", "funda", "edredón", "colcha", "protector de colchón"),
        "cocina" to listOf("cocina", "comedor", "receta", "utensilios", "chef", "cocinar", "alimentos", "ingredientes", "cubiertos", "vaso"),
        "comedor" to listOf("comedor", "mesa", "silla", "salon", "mantel", "vajilla", "cubiertos", "centro de mesa", "terraza"),
        "ventanas" to listOf("cortina", "persiana", "ventana", "tratamiento", "estor", "paqueto", "visillos", "panel"),
        "decoracion" to listOf("decoración", "diseño", "interior", "adornos", "decora", "piso", "casa", "cuadros", "velas", "figuras"),
        "electrodomesticos-cocina" to listOf(
            "microondas", "horno", "licuadora", "batidora", "tostadora", "freidora", "cafetera", "lavavajillas", "refrigerador", "congelador",
            "olla de cocción lenta", "olla a presión", "robot de cocina", "extractor de jugos", "batidora de mano", "grill", "sándwichera",
            "máquina de pan", "vaporizador de alimentos", "espumador de leche", "procesador de alimentos", "hornillo", "plancha eléctrica",
            "rallador eléctrico", "picadora de carne", "molino de café", "secador de frutas", "heladera", "máquina para hacer pasta", "arrocera",
            "hervidor eléctrico", "molinillo de especias", "crepera", "fondue eléctrica", "freidora de aire", "deshidratador de alimentos",
            "moldeador de gofres", "horno tostador", "termo de cocina", "sandwichera", "gofrera", "cocina de inducción", "planchadora de tortillas", "electrodoméstico"
        ),
        "iluminacion" to listOf("iluminación", "lámpara", "luz", "bombilla", "led", "foco", "pantalla", "aplique", "candelabro", "linternaERNA"),
        "jardin" to listOf("jardín", "exterior", "buganvilla", "plantas", "césped", "hortensia", "peonía", "planta", "plantar", "cactus", "flor", "manguera", "riego", "terraza", "patio"),
        "limpieza" to listOf("limpieza", "higiene", "detergente", "productos", "limpiar", "escoba", "fregona", "desinfectante", "barrer", "trapeador"),
        "muebles" to listOf("mobiliario", "muebles", "silla", "mesa", "habitacion", "dormitorio", "cómoda", "armario", "escritorio", "butaca", "taburete"),
        "papeles" to listOf("papel tapiz", "estucado", "pared", "empapelar", "decorativo", "mural", "vinilo", "adhesivo"),
        "electrodomesticos-pequenos" to listOf("electrodomésticos", "cafetera", "licuadora", "batidora", "tostadora", "hervidor", "exprimidor", "plancha", "rizador"),
        "plancha" to listOf("plancha", "ropa", "vaporización", "arrugas", "tablas de planchar", "planchado", "vapor", "prensa"),
        "mascotas" to listOf("mascotas", "perro", "gato", "accesorios", "alimentos", "camitas", "juguetes", "correas", "transportín"),
        "relojes" to listOf("reloj", "despertador", "cronómetro", "digital", "analógico", "pulsera"),
        "reparacion" to listOf("reparación", "mejora", "hogar", "servicios", "herramientas", "bricolaje", "taller", "arreglar", "mantenimiento"),
        "recetas" to listOf("receta", "comida", "plato", "alimento", "cena", "desayuno", "almuerzo", "postre", "ingredientes"),
        "navidad" to listOf(
            "navidad", "árbol de navidad", "adornos navideños", "luces navideñas", "decoración navideña",
            "guirnaldas", "nacimiento", "belenes", "figuras navideñas", "regalos", "villancicos",
            "santa claus", "papá noel", "renos", "muérdago", "corona de adviento", "calcetines navideños",
            "turrón", "mazapán", "gorro de navidad", "estrella navideña", "vela navideña",
            "trineo", "duendes", "nieve", "postales navideñas", "año nuevo", "noche buena", "noche vieja", "reyes magos", "san esteban"
        )
    )
}