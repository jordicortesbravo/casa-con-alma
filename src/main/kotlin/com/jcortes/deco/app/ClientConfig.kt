package com.jcortes.deco.app

import com.jcortes.deco.client.UnsplashClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
class ClientConfig {

    @Bean
    fun unsplashClient(): UnsplashClient {
        val baseUrl = "https://api.unsplash.com"
        val apiKey = "4sRdAfNFOiOn4fprUf8a3wYQNDWe2bdC9GQVnR612Wk"
        val downloadBaseDir = File("/Users/jcortes/workspace/crawler/images")
        return UnsplashClient(downloadBaseDir, baseUrl, apiKey)
    }
}