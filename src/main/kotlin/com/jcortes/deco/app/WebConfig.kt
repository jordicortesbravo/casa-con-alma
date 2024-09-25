package com.jcortes.deco.app

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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

    @Value("\${app.base-path}")
    private lateinit var basePath: String

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("$basePath/static/**")
            .addResourceLocations("classpath:/web/static/")
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("$basePath/**")
            .allowedOriginPatterns(*allowedOrigins.toTypedArray())
            .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
            .allowCredentials(true).maxAge(3600)
    }

    @Bean
    fun templateResolver() = ClassLoaderTemplateResolver().apply {
        prefix = "web/templates/"
        suffix = ".html"
        setTemplateMode("HTML")
        characterEncoding = "UTF-8"
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
}
