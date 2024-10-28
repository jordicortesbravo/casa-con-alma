package com.jcortes.deco.content.model

enum class DecorTag(val label: String, val seoUrl: String, val description: String) {
    LIVING_ROOM_DECOR("Decoración de salones","temas/decoracion-salones", DecorTagDescriptions.LIVING_ROOM_DECOR_DESCRIPTION),
    DINING_ROOM_DECOR("Decoración de comedores","temas/decoracion-comedores", DecorTagDescriptions.DINING_ROOM_DECOR_DESCRIPTION),
    LIVING_ROOM_FURNITURE("Muebles de salón","temas/muebles-salon", DecorTagDescriptions.LIVING_ROOM_FURNITURE_DESCRIPTION),
    SOFAS_ARMCHAIRS("Sofás y sillones","temas/sofas-sillones", DecorTagDescriptions.SOFAS_ARMCHAIRS_DESCRIPTION),
    DINING_TABLES("Mesas de comedor","temas/mesas-comedor", DecorTagDescriptions.DINING_TABLES_DESCRIPTION),
    INTERIOR_DECOR("Decoración de interiores","temas/decoracion-interiores", DecorTagDescriptions.INTERIOR_DECOR_DESCRIPTION),
    INTERIOR_COLORS("Colores para interiores","temas/colores-interiores", DecorTagDescriptions.INTERIOR_COLORS_DESCRIPTION),
    INTERIOR_LIGHTING("Iluminación en interiores","temas/iluminacion-interiores", DecorTagDescriptions.INTERIOR_LIGHTING_DESCRIPTION),
    DECOR_STYLES("Estilos de decoración","temas/estilos-decoracion", DecorTagDescriptions.DECOR_STYLES_DESCRIPTION),
    MODERN_DECOR("Decoración moderna","temas/decoracion-moderna", DecorTagDescriptions.MODERN_DECOR_DESCRIPTION),
    RUSTIC_DECOR("Decoración rústica","temas/decoracion-rustica", DecorTagDescriptions.RUSTIC_DECOR_DESCRIPTION),
    MINIMALIST_DECOR("Decoración minimalista","temas/decoracion-minimalista", DecorTagDescriptions.MINIMALIST_DECOR_DESCRIPTION),
    VINTAGE_DECOR("Decoración vintage","temas/decoracion-vintage", DecorTagDescriptions.VINTAGE_DECOR_DESCRIPTION),
    HOME_TEXTILES("Textiles para el hogar","temas/textiles-hogar", DecorTagDescriptions.HOME_TEXTILES_DESCRIPTION),
    CURTAINS_BLINDS("Cortinas y estores","temas/cortinas-estores", DecorTagDescriptions.CURTAINS_BLINDS_DESCRIPTION),
    RUGS_TAPESTRIES("Alfombras y tapices","temas/alfombras-tapices", DecorTagDescriptions.RUGS_TAPESTRIES_DESCRIPTION),
    DECORATIVE_CUSHIONS("Cojines decorativos","temas/cojines-decorativos", DecorTagDescriptions.DECORATIVE_CUSHIONS_DESCRIPTION),
    PLANT_DECOR("Decoración con plantas","temas/decoracion-plantas", DecorTagDescriptions.PLANT_DECOR_DESCRIPTION),
    WOOD_DECOR("Decoración con madera","temas/decoracion-madera", DecorTagDescriptions.WOOD_DECOR_DESCRIPTION),
    SHELVES_BOOKCASES("Estanterías y librerías","temas/estanterias-librerias", DecorTagDescriptions.SHELVES_BOOKCASES_DESCRIPTION),
    HOME_ORGANIZATION("Organización en el hogar","temas/organizacion-hogar", DecorTagDescriptions.HOME_ORGANIZATION_DESCRIPTION),
    SMALL_SPACES("Espacios pequeños","temas/espacios-pequenos", DecorTagDescriptions.SMALL_SPACES_DESCRIPTION),
    KITCHEN_DECOR("Decoración de cocinas","temas/decoracion-cocinas", DecorTagDescriptions.KITCHEN_DECOR_DESCRIPTION),
    OPEN_KITCHENS("Cocinas abiertas","temas/cocinas-abiertas", DecorTagDescriptions.OPEN_KITCHENS_DESCRIPTION),
    KITCHEN_COLORS("Colores para cocinas","temas/colores-cocinas", DecorTagDescriptions.KITCHEN_COLORS_DESCRIPTION),
    KITCHEN_FURNITURE("Muebles de cocina","temas/muebles-cocina", DecorTagDescriptions.KITCHEN_FURNITURE_DESCRIPTION),
    COUNTERTOPS_SURFACES("Encimeras y superficies","temas/encimeras-superficies", DecorTagDescriptions.COUNTERTOPS_SURFACES_DESCRIPTION),
    KITCHEN_LAYOUT("Distribución de cocinas","temas/distribucion-cocinas", DecorTagDescriptions.KITCHEN_LAYOUT_DESCRIPTION),
    KITCHEN_LIGHTING("Iluminación en cocinas","temas/iluminacion-cocinas", DecorTagDescriptions.KITCHEN_LIGHTING_DESCRIPTION),
    MODERN_KITCHENS("Cocinas modernas","temas/cocinas-modernas", DecorTagDescriptions.MODERN_KITCHENS_DESCRIPTION),
    RUSTIC_KITCHENS("Cocinas rústicas","temas/cocinas-rusticas", DecorTagDescriptions.RUSTIC_KITCHENS_DESCRIPTION),
    KIDS_BEDROOMS("Dormitorios infantiles","temas/dormitorios-infantiles", DecorTagDescriptions.KIDS_BEDROOMS_DESCRIPTION),
    BEDROOM_DECOR("Decoración de dormitorios","temas/decoracion-dormitorios", DecorTagDescriptions.BEDROOM_DECOR_DESCRIPTION),
    BED_HEADBOARDS("Cabeceros de cama","temas/cabeceros-cama", DecorTagDescriptions.BED_HEADBOARDS_DESCRIPTION),
    BEDDING("Ropa de cama","temas/ropa-cama", DecorTagDescriptions.BEDDING_DESCRIPTION),
    BEDROOM_LIGHTING("Iluminación en dormitorios","temas/iluminacion-dormitorios", DecorTagDescriptions.BEDROOM_LIGHTING_DESCRIPTION),
    WARDROBES_CLOSET("Armarios y vestidores","temas/armarios-vestidores", DecorTagDescriptions.WARDROBES_CLOSET_DESCRIPTION),
    MODERN_BEDROOMS("Dormitorios modernos","temas/dormitorios-modernos", DecorTagDescriptions.MODERN_BEDROOMS_DESCRIPTION),
    RUSTIC_BEDROOMS("Dormitorios rústicos","temas/dormitorios-rusticos", DecorTagDescriptions.RUSTIC_BEDROOMS_DESCRIPTION),
    MINIMALIST_BEDROOMS("Dormitorios minimalistas","temas/dormitorios-minimalistas", DecorTagDescriptions.MINIMALIST_BEDROOMS_DESCRIPTION),
    BATHROOM_DECOR("Decoración de baños","temas/decoracion-banos", DecorTagDescriptions.BATHROOM_DECOR_DESCRIPTION),
    BATHROOM_COLORS("Colores para baños","temas/colores-banos", DecorTagDescriptions.BATHROOM_COLORS_DESCRIPTION),
    SMALL_BATHROOMS("Baños pequeños","temas/banos-pequenos", DecorTagDescriptions.SMALL_BATHROOMS_DESCRIPTION),
    TILES_CERAMICS("Azulejos y cerámica","temas/azulejos-ceramica", DecorTagDescriptions.TILES_CERAMICS_DESCRIPTION),
    BATHROOM_FURNITURE("Muebles de baño","temas/muebles-bano", DecorTagDescriptions.BATHROOM_FURNITURE_DESCRIPTION),
    BATHROOM_ACCESSORIES("Accesorios de baño","temas/accesorios-bano", DecorTagDescriptions.BATHROOM_ACCESSORIES_DESCRIPTION),
    BATHROOM_LIGHTING("Iluminación en baños","temas/iluminacion-banos", DecorTagDescriptions.BATHROOM_LIGHTING_DESCRIPTION),
    MODERN_BATHROOMS("Baños modernos","temas/banos-modernos", DecorTagDescriptions.MODERN_BATHROOMS_DESCRIPTION),
    RUSTIC_BATHROOMS("Baños rústicos","temas/banos-rusticos", DecorTagDescriptions.RUSTIC_BATHROOMS_DESCRIPTION),
    GARDENS_TERRACES("Jardines y terrazas","temas/jardines-terrazas", DecorTagDescriptions.GARDEN_DECOR_DESCRIPTION),
    OUTDOOR_DECOR("Decoración de exteriores","temas/decoracion-exteriores", DecorTagDescriptions.OUTDOOR_DECOR_DESCRIPTION),
    OUTDOOR_FURNITURE("Muebles de exterior","temas/muebles-exterior", DecorTagDescriptions.OUTDOOR_FURNITURE_DESCRIPTION),
    PLANTS_FLOWERS("Plantas y flores","temas/plantas-flores", DecorTagDescriptions.PLANTS_FLOWERS_DESCRIPTION),
    OUTDOOR_LIGHTING("Iluminación exterior","temas/iluminacion-exterior", DecorTagDescriptions.OUTDOOR_LIGHTING_DESCRIPTION),
    SMALL_TERRACES("Terrazas pequeñas","temas/terrazas-pequenas", DecorTagDescriptions.SMALL_TERRACES_DESCRIPTION),
    PATIOS_GARDENS("Patios y jardines","temas/patios-jardines", DecorTagDescriptions.PATIOS_GARDENS_DESCRIPTION),
    PERGOLAS_AWNINGS("Pérgolas y toldos","temas/pergolas-toldos", DecorTagDescriptions.PERGOLAS_AWNINGS_DESCRIPTION),
    SEASONAL_DECOR("Decoración estacional","temas/decoracion-estacional", DecorTagDescriptions.SEASONAL_DECOR_DESCRIPTION),
    CHRISTMAS_DECOR("Decoración de Navidad","temas/decoracion-navidad", DecorTagDescriptions.CHRISTMAS_DECOR_DESCRIPTION),
    SPRING_DECOR("Decoración de primavera","temas/decoracion-primavera", DecorTagDescriptions.SPRING_DECOR_DESCRIPTION),
    SUMMER_DECOR("Decoración de verano","temas/decoracion-verano", DecorTagDescriptions.SUMMER_DECOR_DESCRIPTION),
    FALL_DECOR("Decoración de otoño","temas/decoracion-otono", DecorTagDescriptions.FALL_DECOR_DESCRIPTION),
    DECOR_TRENDS("Tendencias de decoración","temas/tendencias-decoracion", DecorTagDescriptions.DECOR_TRENDS_DESCRIPTION),
    DIY_DECOR("DIY decoración","temas/diy-decoracion", DecorTagDescriptions.DIY_DECOR_DESCRIPTION),
    LOW_COST_DECOR("Decoración low cost","temas/decoracion-low-cost", DecorTagDescriptions.LOW_COST_DECOR_DESCRIPTION),
    RECYCLED_DECOR("Reciclaje y decoración","temas/reciclaje-decoracion", DecorTagDescriptions.RECYCLED_DECOR_DESCRIPTION),
    DECOR_ART("Arte y cuadros decorativos","temas/arte-cuadros-decorativos", DecorTagDescriptions.DECOR_ART_DESCRIPTION),
    DECOR_MIRRORS("Espejos decorativos","temas/espejos-decorativos", DecorTagDescriptions.DECOR_MIRRORS_DESCRIPTION),
    WALLPAPERS("Papeles pintados","temas/papeles-pintados", DecorTagDescriptions.WALLPAPERS_DESCRIPTION),
    WALLS_COVERINGS("Paredes y revestimientos","temas/paredes-revestimientos", DecorTagDescriptions.WALLS_COVERINGS_DESCRIPTION),
    HOME_RENOVATIONS("Reformas del hogar","temas/reformas-hogar", DecorTagDescriptions.HOME_RENOVATIONS_DESCRIPTION),
    SPACE_LAYOUT("Distribución de espacios","temas/distribucion-espacios", DecorTagDescriptions.SPACE_LAYOUT_DESCRIPTION),
    INTERIOR_DESIGN("Diseño de interiores","temas/diseno-interiores", DecorTagDescriptions.INTERIOR_DESIGN_DESCRIPTION),
    HALLOWEEN_DECOR("Decoración de Halloween","temas/decoracion-halloween", DecorTagDescriptions.HALLOWEEN_DECOR_DESCRIPTION),;

    companion object {
        fun fromLabel(label: String): DecorTag? {
            return entries.find { it.label.lowercase() == label.lowercase() }
        }
    }
}

private object DecorTagDescriptions {
    const val LIVING_ROOM_DECOR_DESCRIPTION = "La decoración de salones es clave para crear un espacio acogedor y funcional. Descubre ideas y consejos para combinar muebles, colores y elementos decorativos que transformarán tu salón en el corazón de tu hogar."
    const val DINING_ROOM_DECOR_DESCRIPTION = "La decoración de comedores es esencial para crear un ambiente agradable y funcional. Explora diferentes estilos, colores y diseños de muebles para que tus comidas sean una experiencia placentera."
    const val LIVING_ROOM_FURNITURE_DESCRIPTION = "Elige los mejores muebles de salón para combinar comodidad y estilo. Sofás, mesas y estanterías que se adaptan a tu espacio y personalidad, creando un ambiente único en tu hogar."
    const val SOFAS_ARMCHAIRS_DESCRIPTION = "Los sofás y sillones son los protagonistas del salón. Descubre diseños cómodos y elegantes que se adaptan a tu estilo de vida y aportan confort a tu hogar."
    const val DINING_TABLES_DESCRIPTION = "Las mesas de comedor son el centro de atención en tus reuniones. Encuentra la mesa perfecta que combine diseño, funcionalidad y estilo para todo tipo de espacios."
    const val INTERIOR_DECOR_DESCRIPTION = "La decoración de interiores transforma tu hogar en un espacio personalizado y acogedor. Descubre ideas de estilo, colores y mobiliario para renovar cualquier habitación."
    const val INTERIOR_COLORS_DESCRIPTION = "Los colores para interiores son esenciales para definir el ambiente de tu hogar. Aprende a combinarlos para crear espacios armónicos y llenos de personalidad."
    const val INTERIOR_LIGHTING_DESCRIPTION = "La iluminación en interiores juega un papel fundamental en la ambientación. Conoce las mejores opciones para iluminar tus espacios y realzar la decoración."
    const val DECOR_STYLES_DESCRIPTION = "Explora los diferentes estilos de decoración, desde el minimalismo hasta el rústico, y aprende cómo aplicarlos en tu hogar para reflejar tu personalidad y gustos."
    const val MODERN_DECOR_DESCRIPTION = "La decoración moderna se caracteriza por su simplicidad y elegancia. Conoce cómo integrar líneas limpias, colores neutros y materiales innovadores en tu hogar."
    const val RUSTIC_DECOR_DESCRIPTION = "La decoración rústica aporta calidez y naturalidad a cualquier espacio. Descubre cómo utilizar madera, textiles naturales y tonos cálidos para crear un ambiente acogedor."
    const val MINIMALIST_DECOR_DESCRIPTION = "La decoración minimalista se basa en la simplicidad y funcionalidad. Aprende a reducir lo innecesario y a destacar lo esencial en cada rincón de tu hogar."
    const val VINTAGE_DECOR_DESCRIPTION = "La decoración vintage se inspira en el pasado para dar un toque nostálgico y único a tu hogar. Conoce cómo mezclar piezas antiguas con un estilo moderno."
    const val HOME_TEXTILES_DESCRIPTION = "Los textiles para el hogar aportan color y textura a cualquier espacio. Cojines, cortinas, alfombras y mantas que transforman el ambiente de tu casa."
    const val CURTAINS_BLINDS_DESCRIPTION = "Las cortinas y estores son elementos clave en la decoración. Aprende a elegir las opciones adecuadas para controlar la luz y aportar estilo a tus habitaciones."
    const val RUGS_TAPESTRIES_DESCRIPTION = "Las alfombras y tapices no solo visten tus suelos, también son una excelente opción para agregar calidez y personalidad a tus espacios."
    const val DECORATIVE_CUSHIONS_DESCRIPTION = "Los cojines decorativos son el complemento perfecto para añadir color y comodidad a tu hogar. Descubre las mejores combinaciones para realzar tus muebles."
    const val PLANT_DECOR_DESCRIPTION = "La decoración con plantas aporta vida y frescura a cualquier ambiente. Aprende a integrar plantas en la decoración de tu hogar para crear un espacio más natural y acogedor."
    const val WOOD_DECOR_DESCRIPTION = "La decoración con madera aporta calidez y un toque natural a tus espacios. Descubre cómo utilizar este material para crear ambientes acogedores y elegantes."
    const val SHELVES_BOOKCASES_DESCRIPTION = "Las estanterías y librerías no solo son funcionales, también decoran tus espacios. Encuentra opciones que se adapten a tu estilo y necesidades de almacenamiento."
    const val HOME_ORGANIZATION_DESCRIPTION = "La organización en el hogar es fundamental para mantener el orden y la funcionalidad. Descubre consejos prácticos para organizar cada rincón de tu casa."
    const val SMALL_SPACES_DESCRIPTION = "Los espacios pequeños pueden ser grandes en estilo. Aprende a maximizar el espacio y a decorar eficientemente sin sacrificar funcionalidad ni estética."
    const val KITCHEN_DECOR_DESCRIPTION = "La decoración de cocinas combina estilo y funcionalidad. Explora ideas para crear un espacio donde cocinar sea un placer y el diseño se refleje en cada detalle."
    const val OPEN_KITCHENS_DESCRIPTION = "Las cocinas abiertas integran funcionalidad y diseño en un solo espacio. Descubre cómo crear un ambiente fluido entre cocina y sala de estar."
    const val KITCHEN_COLORS_DESCRIPTION = "Los colores para cocinas transforman el ambiente y la sensación de este espacio tan importante en el hogar. Encuentra la combinación perfecta para tu estilo."
    const val KITCHEN_FURNITURE_DESCRIPTION = "El mobiliario de cocina debe ser funcional y estético. Descubre las mejores opciones de muebles que maximizan el espacio y mejoran la organización en tu cocina."
    const val COUNTERTOPS_SURFACES_DESCRIPTION = "Las encimeras y superficies de cocina son clave tanto en diseño como en funcionalidad. Conoce las mejores opciones para cada estilo y tipo de uso."
    const val KITCHEN_LAYOUT_DESCRIPTION = "La distribución de cocinas afecta tanto la funcionalidad como el diseño. Aprende a crear una cocina práctica y hermosa con la disposición adecuada de los elementos."
    const val KITCHEN_LIGHTING_DESCRIPTION = "La iluminación en cocinas es esencial para crear un ambiente funcional y acogedor. Descubre las mejores soluciones para iluminar adecuadamente tu espacio."
    const val MODERN_KITCHENS_DESCRIPTION = "Las cocinas modernas destacan por su diseño innovador y funcional. Conoce cómo combinar tecnología y estilo en uno de los espacios más importantes del hogar."
    const val RUSTIC_KITCHENS_DESCRIPTION = "Las cocinas rústicas ofrecen un ambiente cálido y acogedor. Descubre cómo integrar madera y colores naturales para lograr una cocina que inspire tradición y confort."
    const val KIDS_BEDROOMS_DESCRIPTION = "Los dormitorios infantiles deben ser espacios funcionales y divertidos. Descubre cómo decorar y organizar el cuarto de los más pequeños con estilo y creatividad."
    const val BEDROOM_DECOR_DESCRIPTION = "La decoración de dormitorios es esencial para crear un espacio de descanso. Aprende a combinar colores, muebles y textiles que fomenten la tranquilidad y el confort."
    const val BED_HEADBOARDS_DESCRIPTION = "Los cabeceros de cama son un elemento decorativo clave en el dormitorio. Encuentra opciones que complementen tu estilo y aporten personalidad a tu habitación."
    const val BEDDING_DESCRIPTION = "La ropa de cama es un aspecto esencial en la decoración de dormitorios. Descubre las mejores opciones para combinar estilo y confort en tu descanso diario."
    const val BEDROOM_LIGHTING_DESCRIPTION = "La iluminación en dormitorios influye en el ambiente de descanso. Aprende a elegir las luces adecuadas para crear un espacio acogedor y relajante."
    const val WARDROBES_CLOSET_DESCRIPTION = "Los armarios y vestidores ayudan a organizar tu ropa y accesorios de forma eficiente. Descubre ideas para optimizar el espacio y mantener todo en orden."
    const val MODERN_BEDROOMS_DESCRIPTION = "Los dormitorios modernos destacan por su simplicidad y funcionalidad. Descubre cómo aplicar este estilo para crear un ambiente elegante y acogedor."
    const val RUSTIC_BEDROOMS_DESCRIPTION = "Los dormitorios rústicos invitan a la relajación y el confort. Aprende a decorar tu habitación con materiales naturales y tonos cálidos para un ambiente acogedor."
    const val MINIMALIST_BEDROOMS_DESCRIPTION = "Los dormitorios minimalistas apuestan por lo esencial. Conoce cómo reducir el desorden y crear un espacio de descanso con líneas limpias y colores suaves."
    const val BATHROOM_DECOR_DESCRIPTION = "La decoración de baños combina funcionalidad y estilo. Aprende a optimizar el espacio y a elegir los mejores accesorios para crear un baño moderno y cómodo."
    const val BATHROOM_COLORS_DESCRIPTION = "Los colores para baños influyen en la percepción del espacio y la limpieza. Descubre combinaciones que realcen la luminosidad y frescura de tu baño."
    const val SMALL_BATHROOMS_DESCRIPTION = "Los baños pequeños también pueden ser funcionales y estéticos. Descubre cómo aprovechar al máximo el espacio con ideas creativas y soluciones de diseño."
    const val TILES_CERAMICS_DESCRIPTION = "Los azulejos y la cerámica son esenciales en la decoración de baños y cocinas. Aprende a elegir los mejores materiales y diseños para estos espacios."
    const val BATHROOM_FURNITURE_DESCRIPTION = "Los muebles de baño deben combinar practicidad y estilo. Descubre opciones de almacenamiento y diseño que optimicen tu baño y lo hagan más funcional."
    const val BATHROOM_ACCESSORIES_DESCRIPTION = "Los accesorios de baño añaden el toque final a la decoración. Aprende a elegir elementos funcionales y decorativos que se adapten a tu estilo."
    const val BATHROOM_LIGHTING_DESCRIPTION = "La iluminación en baños es clave para crear un ambiente funcional y relajante. Descubre las mejores opciones para iluminar tu baño correctamente."
    const val MODERN_BATHROOMS_DESCRIPTION = "Los baños modernos destacan por su elegancia y funcionalidad. Descubre cómo aplicar este estilo con líneas limpias, materiales innovadores y colores neutros."
    const val RUSTIC_BATHROOMS_DESCRIPTION = "Los baños rústicos ofrecen calidez y confort. Aprende a decorar con materiales naturales y tonos tierra para crear un espacio de relajación único."
    const val OUTDOOR_DECOR_DESCRIPTION = "La decoración exterior transforma tu jardín o terraza en un oasis personal. Descubre ideas para combinar muebles y plantas, creando un ambiente perfecto al aire libre."
    const val GARDEN_DECOR_DESCRIPTION = "La decoración de jardines es esencial para crear un espacio exterior acogedor. Aprende a integrar muebles, plantas y elementos decorativos que transformen tu jardín en un refugio personal."
    const val OUTDOOR_FURNITURE_DESCRIPTION = "Muebles resistentes a las inclemencias del tiempo, ideales para patios, terrazas y jardines."
    const val PLANTS_FLOWERS_DESCRIPTION = "Plantas y flores que embellecen el hogar y mejoran la calidad del aire."
    const val OUTDOOR_LIGHTING_DESCRIPTION = "Soluciones de iluminación diseñadas para espacios exteriores, que crean atmósferas acogedoras."
    const val SMALL_TERRACES_DESCRIPTION = "Ideas y soluciones para maximizar el espacio en terrazas pequeñas."
    const val PATIOS_GARDENS_DESCRIPTION = "Consejos para diseñar y mantener patios y jardines atractivos."
    const val PERGOLAS_AWNINGS_DESCRIPTION = "Estructuras que proporcionan sombra y estilo a los espacios exteriores."
    const val SEASONAL_DECOR_DESCRIPTION = "Decoración que cambia con las estaciones, añadiendo frescura al hogar."
    const val CHRISTMAS_DECOR_DESCRIPTION = "Ideas para decorar el hogar durante la Navidad, creando un ambiente festivo."
    const val SPRING_DECOR_DESCRIPTION = "Decoraciones que reflejan la frescura y los colores vibrantes de la primavera."
    const val SUMMER_DECOR_DESCRIPTION = "Elementos decorativos que evocan el calor y la alegría del verano."
    const val FALL_DECOR_DESCRIPTION = "Decoración inspirada en los tonos cálidos y acogedores del otoño."
    const val DECOR_TRENDS_DESCRIPTION = "Tendencias actuales en decoración que marcan el estilo del momento."
    const val DIY_DECOR_DESCRIPTION = "Proyectos de decoración que puedes hacer tú mismo, añadiendo un toque personal."
    const val LOW_COST_DECOR_DESCRIPTION = "Ideas para decorar sin gastar mucho, ideales para presupuestos ajustados."
    const val RECYCLED_DECOR_DESCRIPTION = "Uso de materiales reciclados para crear piezas decorativas únicas."
    const val DECOR_ART_DESCRIPTION = "Obras de arte y cuadros decorativos que aportan personalidad a los espacios."
    const val DECOR_MIRRORS_DESCRIPTION = "Espejos que no solo son funcionales, sino que también sirven como elementos decorativos."
    const val WALLPAPERS_DESCRIPTION = "Papeles pintados que transforman las paredes y añaden carácter a cualquier habitación."
    const val WALLS_COVERINGS_DESCRIPTION = "Materiales y técnicas para revestir paredes de manera estética y funcional."
    const val HOME_RENOVATIONS_DESCRIPTION = "Ideas y consejos para realizar reformas en el hogar, mejorando su funcionalidad y estética."
    const val SPACE_LAYOUT_DESCRIPTION = "Estrategias para organizar y distribuir el espacio en el hogar de manera eficiente."
    const val INTERIOR_DESIGN_DESCRIPTION = "Conceptos y técnicas de diseño de interiores que optimizan la estética y funcionalidad de los espacios."
    const val HALLOWEEN_DECOR_DESCRIPTION = "Ideas para decorar tu hogar en Halloween y crear un ambiente terrorífico."
}
