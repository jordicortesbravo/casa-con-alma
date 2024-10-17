package com.jcortes.deco.app

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration
import software.amazon.awssdk.http.SdkHttpConfigurationOption
import software.amazon.awssdk.http.apache.ApacheHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient
import software.amazon.awssdk.utils.AttributeMap
import java.time.Duration


@Configuration
class AwsConfig {

    @Bean
    fun bedrockRuntimeClient(): BedrockRuntimeClient {

        val timeoutConfig = AttributeMap.builder()
            .put(SdkHttpConfigurationOption.READ_TIMEOUT, Duration.ofMinutes(5))
            .build()

        val httpClient = ApacheHttpClient.builder()
            .buildWithDefaults(timeoutConfig)

        return BedrockRuntimeClient.builder()
            .httpClient(httpClient)
            .overrideConfiguration(ClientOverrideConfiguration.builder()
                .apiCallTimeout(null)
                .build())
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
