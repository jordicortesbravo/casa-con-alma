package com.jcortes.deco.content.model

enum class SiteCategory(val label: String, val seoUrl: String) {
    DECORATION("Decoración", "decoracion"),
    LIVING_AND_DINING_ROOMS("Salones", "salones-y-comedores"),
    KITCHENS("Cocinas", "cocinas"),
    BEDROOMS("Dormitorios", "dormitorios"),
    BATHROOMS("Baños", "banos"),
    OUTDOORS_AND_GARDENS("Exteriores", "exteriores-y-jardines"),
    SEASONAL_DECORATION("Festividades", "decoracion-estacional")
}