package com.jcortes.deco.web

import com.jcortes.deco.content.ArticleService
import com.jcortes.deco.content.ImageService
import com.jcortes.deco.content.model.Article
import com.jcortes.deco.content.model.DecorTag
import com.jcortes.deco.content.model.SiteCategory
import com.jcortes.deco.util.Pageable
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("\${app.base-path}")
class ArticleDetailController(
    private val articleService: ArticleService,
    private val imageService: ImageService
) {

    @GetMapping("/article/{id}")
    fun detail(@PathVariable id: Long, model: Model): String {
        val article = articleService.get(id)

//        article.content =
//            "<h2>${article.title}</h2><p>Organizar una habitación infantil puede parecer una tarea desafiante, pero con un enfoque adecuado, se puede crear un espacio armonioso y fácil de mantener. **La clave está en maximizar el almacenamiento** y diseñar áreas que estimulen el orden y la autonomía del niño.</p><img data-search=\"habitaciones de niños con espacio de almacenamiento\" data-ia-prompt=\"Una habitación infantil elegante y rústica, con tonos beige, blanco y detalles en madera, con estanterías y cajones de almacenamiento. Estilo acogedor y minimalista.\" caption=\"El almacenamiento eficiente ayuda a mantener la habitación organizada y funcional.\"><h3>1. Mobiliario multifuncional: la clave del orden</h3><p>El uso de muebles multifuncionales es esencial en las habitaciones de los niños. Por ejemplo, una cama con cajones integrados o una mesa que se convierta en escritorio puede **maximizar el espacio disponible**. Esto también ayuda a mantener los juguetes, libros y ropa en su lugar.</p><img data-search=\"cama infantil con cajones de almacenamiento\" data-ia-prompt=\"Cama infantil de madera con cajones incorporados debajo, rodeada de una decoración elegante en tonos beige y madera. Espacio organizado con toques rústicos.\" caption=\"Una cama con cajones integrados maximiza el espacio de almacenamiento sin sacrificar el diseño.\"><h3>2. Estanterías abiertas: a la vista, pero ordenado</h3><p>Las estanterías abiertas son una opción fantástica para **mantener los objetos al alcance del niño** y al mismo tiempo fomentar la organización. Puedes usarlas para libros, juguetes o adornos, creando un ambiente visualmente atractivo y accesible.</p><img data-search=\"estanterías abiertas en habitaciones infantiles\" data-ia-prompt=\"Estanterías abiertas en una habitación infantil rústica, decorada con juguetes y libros, con tonos claros de beige y blanco. Ambiente ordenado y cálido.\" caption=\"Las estanterías abiertas permiten a los niños ver y acceder fácilmente a sus pertenencias.\"><h3>3. Zonas específicas para diferentes actividades</h3><p>Una forma efectiva de mantener el orden en una habitación infantil es **crear zonas dedicadas a diferentes actividades**: una para el juego, otra para el estudio y otra para el descanso. Cada zona debe estar equipada con los elementos necesarios para esa actividad, lo que facilitará la transición de una tarea a otra sin desorden.</p><img data-search=\"zonas de juego y estudio en habitaciones infantiles\" data-ia-prompt=\"Habitación infantil elegante con zonas separadas para estudio y juego. Decoración en tonos cálidos de madera y beige, con una mesa de estudio y un rincón de juegos acogedor.\" caption=\"Separar las zonas de estudio y juego ayuda a mantener un ambiente más organizado y funcional.\"><h3>4. Almacenamiento vertical para aprovechar el espacio</h3><p>Aprovechar las paredes es una excelente manera de **optimizar el espacio en habitaciones pequeñas**. Los ganchos, estanterías altas y organizadores de pared permiten liberar el suelo y aprovechar el espacio vertical, lo que resulta en una habitación más despejada y ordenada.</p><img data-search=\"almacenamiento vertical en habitaciones infantiles\" data-ia-prompt=\"Habitación infantil con soluciones de almacenamiento vertical, como ganchos y estanterías en las paredes, en un entorno rústico y elegante con tonos madera y beige.\" caption=\"El almacenamiento vertical es ideal para maximizar el espacio en habitaciones infantiles reducidas.\"><h3>5. Cestas y contenedores: fácil acceso para los niños</h3><p>El uso de cestas y contenedores es una forma sencilla y práctica de **fomentar la autonomía en los niños**. Ubicados a su altura, estos recipientes permiten que los pequeños puedan guardar sus juguetes y pertenencias por sí mismos, promoviendo el hábito de mantener el orden.</p><img data-search=\"cestas de almacenamiento en habitaciones infantiles\" data-ia-prompt=\"Habitación infantil rústica con cestas de almacenamiento, en tonos naturales de mimbre y beige, organizadas a la altura del niño para facilitar el orden.\" caption=\"Cestas de almacenamiento accesibles para los niños ayudan a fomentar la independencia y el hábito de ordenar.\"><h3>6. Juguetes rotativos: menos es más</h3><p>Uno de los mayores retos en las habitaciones infantiles es la acumulación de juguetes. **Implementar un sistema de rotación** es una excelente estrategia para evitar el caos. Mantén solo algunos juguetes fuera y guarda el resto; cada cierto tiempo, rota los juguetes disponibles. Esto no solo ayuda con la organización, sino que también hace que los niños se interesen más por los juguetes al no verlos constantemente.</p><img data-search=\"organización de juguetes en habitaciones infantiles\" data-ia-prompt=\"Habitación infantil rústica y elegante con estanterías de madera y cestas de almacenamiento, donde los juguetes están organizados de manera rotativa. Tonos beige y blanco predominantes.\" caption=\"La rotación de juguetes mantiene el espacio ordenado y estimula el interés continuo de los niños.\"><h3>Conclusión: un espacio organizado, un ambiente saludable</h3><p>La organización en una habitación infantil es fundamental para crear un entorno que sea tanto funcional como estéticamente agradable. **Con el uso de muebles multifuncionales, estanterías abiertas, almacenamiento vertical y zonas diferenciadas**, es posible diseñar un espacio que no solo sea fácil de mantener, sino que también fomente el desarrollo y la autonomía del niño.</p>"
        model.addAttribute("detail", detail(article))
        return "public/article-detail"
    }

    private fun detail(article: Article): ArticleDetail {
        val trendingArticles = articleService.getTrending(Pageable(1, 4))
        val relatedArticles = articleService.getTrending(Pageable(1, 40)).sortedBy { it.title }.take(8)
        val tags = article.tags?.take(3) ?: DecorTag.entries.toTypedArray().take(3).map { it.label }

        return ArticleDetail(
            article = article,
            breadcrumbs = breadcrumbs(article),
            relatedArticles = relatedArticles,
            featuredArticle = trendingArticles.first(),
            trendingArticles = trendingArticles.subList(1, trendingArticles.size),
            tags = tags.mapNotNull { DecorTag.fromLabel(it)}.map { ResourceItem(it.label, it.seoUrl) },
            seo = seo(article)
        )
    }

    private fun breadcrumbs(article: Article): List<ResourceItem> {
        val category = article.siteCategories?.firstOrNull()
        val breadcrumbs = mutableListOf(ResourceItem("Inicio", ""))
        category?.let { breadcrumbs += ResourceItem(category.label, category.seoUrl) }
        breadcrumbs += ResourceItem(article.title!!, article.seoUrl?.toString() ?: "seo-url-not-found")
        return breadcrumbs
    }

    private fun seo(article: Article): Seo {
        val socialNetworkTags = SocialNetworkTags(
            title = article.title ?: "social-network-title-not-found",
            description = article.description ?: "social-network-description-not-found",
            image = article.images?.firstOrNull()?.url?.toString() ?: "social-network-image-not-found",
            url = article.seoUrl?.toString() ?: "seo-url-not-found"
        )
        val twitterCard = TwitterCard(
            title = article.title ?: "social-network-title-not-found",
            description = article.description ?: "social-network-description-not-found",
            image = article.images?.firstOrNull()?.url?.toString() ?: "social-network-image-not-found",
        )
        return Seo(
            description = article.description ?: "social-network-description-not-found",
            keywords = article.tags?.joinToString(", ") ?: "",
            socialNetworkTags = socialNetworkTags,
            twitterCard = twitterCard,
            canonicalUrl = article.seoUrl?.toString() ?: "seo-url-not-found"
        )
    }

    data class ArticleDetail(
        val categories: List<ResourceItem> = SiteCategory.entries.map { ResourceItem(it.label, it.seoUrl) },
        val article: Article,
        val breadcrumbs: List<ResourceItem>,
        val relatedArticles: List<Article>,
        val featuredArticle: Article,
        val trendingArticles: List<Article>,
        val tags: List<ResourceItem>,
        val seo: Seo
    )

    data class ResourceItem(
        val label: String,
        val url: String
    )

    data class Seo (
        val description: String,
        val keywords: String,
        val charset: String = Charsets.UTF_8.toString(),
        val viewport: String = "width=device-width, initial-scale=1",
        val robots: String = "index, follow",
        val author: String = "Casa con Alma",
        val socialNetworkTags: SocialNetworkTags,
        val twitterCard: TwitterCard,
        val canonicalUrl: String,
        val language: String = "es"
    )

    data class SocialNetworkTags (
        val title: String,
        val description: String,
        val image: String,
        val url: String,
        val type: String = "website"
    )

    data class TwitterCard (
        val card: String = "summary_large_image",
        val site: String = "@casaconalma",
        val creator: String = "@casaconalma",
        val title: String,
        val description: String,
        val image: String
    )
}
