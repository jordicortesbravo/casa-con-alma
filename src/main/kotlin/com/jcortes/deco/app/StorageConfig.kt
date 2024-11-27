package com.jcortes.deco.app

import com.jcortes.deco.tools.util.io.storage.StorageConfiguration
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI


@Configuration
class StorageConfig {

    @Bean
    fun s3Client(publicationProfileCredentialsProvider: AwsCredentialsProvider, publicationProfileRegion: Region): S3Client {
        return S3Client.builder()
            .credentialsProvider(publicationProfileCredentialsProvider)
            .region(publicationProfileRegion)
            .build()
    }

    @Bean
    fun contentStorage(@Value("\${app.content-storage}") storageUri: URI, ctx: ApplicationContext) = StorageConfiguration.createStorage(storageUri, ctx)

    @Bean
    fun imageStorage(@Value("\${app.images-storage}") storageUri: URI, ctx: ApplicationContext) = StorageConfiguration.createStorage(storageUri, ctx)

    @Bean
    fun staticResourcesStorage(@Value("\${app.static-resources-storage}") storageUri: URI, ctx: ApplicationContext) = StorageConfiguration.createStorage(storageUri, ctx)

}