package com.jcortes.deco.app

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.regions.providers.AwsProfileRegionProvider
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AwsConfig {

    @Bean
    fun bedrockRuntimeClient(): BedrockRuntimeClient {
        return BedrockRuntimeClient.builder()
            .credentialsProvider(Credentials.innovationWeekCredentialsProvider())
            .region(Credentials.innovationWeekRegion())
            .build()
    }

    private object Credentials {

        private const val PROFILE_NAME = "Idealista-InnovationWeek"

        fun innovationWeekCredentialsProvider(): AwsCredentialsProvider {
            return ProfileCredentialsProvider.create(PROFILE_NAME)
        }

        fun innovationWeekRegionProvider(): AwsProfileRegionProvider {
            return AwsProfileRegionProvider(null, PROFILE_NAME)
        }

        fun innovationWeekRegion(): Region {
            return innovationWeekRegionProvider().region
        }
    }
}
