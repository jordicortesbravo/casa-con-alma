package com.jcortes.deco.app

import com.jcortes.deco.util.UrlBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.spring6.view.ThymeleafViewResolver
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

@Configuration
class WebConfig : WebMvcConfigurer {

    @Value("\${app.allowed-origins}")
    private lateinit var allowedOrigins: List<String>

    @Value("\${app.content-base-url}")
    private lateinit var contentBaseUrl: String

    @Value("\${app.static-base-url}")
    private lateinit var staticBaseUrl: String

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/static/**")
            .addResourceLocations("classpath:/web/static/")
            .setCachePeriod(3600)

        registry.addResourceHandler("/robots.txt")
            .addResourceLocations("classpath:/web/static/")
            .setCachePeriod(3600)
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns(*allowedOrigins.toTypedArray())
            .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
            .allowCredentials(true).maxAge(3600)
    }

    @Bean
    fun urlBuilder() = UrlBuilder(contentBaseUrl, staticBaseUrl)

    @Bean
    fun templateResolver() = ClassLoaderTemplateResolver().apply {
        prefix = "web/templates/"
        suffix = ".html"
        setTemplateMode("HTML")
        characterEncoding = "UTF-8"
        isCacheable = false
    }

    @Bean
    fun templateEngine() = SpringTemplateEngine().apply {
        setTemplateResolver(templateResolver())
    }

    @Bean
    fun viewResolver() = ThymeleafViewResolver().apply {
        templateEngine = templateEngine()
        characterEncoding = "UTF-8"
    }

    data class ErrorResponse(val message: String)

    @ControllerAdvice
    class DefaultAdvice {

        private val log = LoggerFactory.getLogger(this::class.java)

        @ExceptionHandler(value = [IllegalArgumentException::class, HttpMessageNotReadableException::class])
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        fun badRequest(exception: Exception): ResponseEntity<ErrorResponse> {
            log.error("Bad request", exception)
            return ResponseEntity.badRequest().body(getErrorResponse(exception))
        }

        @ExceptionHandler
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        fun internalServerError(exception: Exception): ResponseEntity<ErrorResponse> {
            log.error("Internal server error", exception)
            return ResponseEntity.internalServerError().body(getErrorResponse(exception))
        }

        private fun getErrorResponse(exception: Exception): ErrorResponse {
            return ErrorResponse(exception.message ?: exception.cause?.message ?: "Unknown reason")
        }
    }
}
