package com.jcortes.deco.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jcortes.deco.tools.util.url.UrlBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
class ServiceConfig {

    @Value("\${app.content-base-url}")
    private lateinit var contentBaseUrl: String

    @Value("\${app.images-base-url}")
    private lateinit var imageBaseUrl: String

    @Value("\${app.static-resources-base-url}")
    private lateinit var staticBaseUrl: String

    @Bean
    fun objectMapper(): ObjectMapper {
        return jacksonObjectMapper().registerModule(JavaTimeModule())
    }

    @Bean
    fun urlBuilder() = UrlBuilder(contentBaseUrl = contentBaseUrl, imageBaseUrl = imageBaseUrl, staticResourcesBaseUrl = staticBaseUrl)
}