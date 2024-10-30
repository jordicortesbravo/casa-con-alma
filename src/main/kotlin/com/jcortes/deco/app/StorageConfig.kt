package com.jcortes.deco.app

import com.jcortes.deco.util.io.storage.StorageConfiguration
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.URI

@Configuration
class StorageConfig {

    @Bean
    fun contentStorage(@Value("\${app.content-storage}") storageUri: URI, ctx: ApplicationContext) = StorageConfiguration.createStorage(storageUri, ctx)

    @Bean
    fun imageStorage(@Value("\${app.images-storage}") storageUri: URI, ctx: ApplicationContext) = StorageConfiguration.createStorage(storageUri, ctx)

    @Bean
    fun staticResourcesStorage(@Value("\${app.static-resources-storage}") storageUri: URI, ctx: ApplicationContext) = StorageConfiguration.createStorage(storageUri, ctx)
}