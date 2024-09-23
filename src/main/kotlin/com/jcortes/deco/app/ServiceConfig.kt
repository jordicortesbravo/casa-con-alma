package com.jcortes.deco.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
class ServiceConfig {

    @Bean
    fun objectMapper(): ObjectMapper {
        return jacksonObjectMapper().registerModule(JavaTimeModule())
    }
}