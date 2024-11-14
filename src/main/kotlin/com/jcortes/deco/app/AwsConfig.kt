package com.jcortes.deco.app

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.regions.Region

@Configuration
class AwsConfig {

    companion object {
        private const val CONTENT_GENERATION_PROFILE_NAME = "YE-Playground"
        private const val PUBLICATION_PROFILE_NAME = "casa-con-alma"
    }

    @Bean
    fun contentProfileCredentialsProvider(): AwsCredentialsProvider {
        return ProfileCredentialsProvider.create(CONTENT_GENERATION_PROFILE_NAME)
    }

    @Bean
    fun contentProfileRegion(): Region {
        return Region.of("us-west-2")
    }

    @Bean
    fun publicationProfileCredentialsProvider(): AwsCredentialsProvider {
        return ProfileCredentialsProvider.create(PUBLICATION_PROFILE_NAME)
    }

    @Bean
    fun publicationProfileRegion(): Region {
        return Region.of("eu-south-2")
    }
}