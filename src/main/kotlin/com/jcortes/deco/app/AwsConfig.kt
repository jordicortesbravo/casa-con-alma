package com.jcortes.deco.app

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient

@Configuration
class AwsConfig {

    @Bean
    fun bedrockRuntimeClient(): BedrockRuntimeClient {
        return BedrockRuntimeClient.builder()
            .credentialsProvider(Credentials.ssoProfileCredentialsProvider())
            .region(Credentials.ssoProfileRegion())
            .build()
    }

    private object Credentials {

        private const val PROFILE_NAME = "YE-Playground"

        fun ssoProfileCredentialsProvider(): AwsCredentialsProvider {
            return ProfileCredentialsProvider.create(PROFILE_NAME)
        }

        fun ssoProfileRegion(): Region {
            return Region.of("us-west-2")
        }
    }
}
