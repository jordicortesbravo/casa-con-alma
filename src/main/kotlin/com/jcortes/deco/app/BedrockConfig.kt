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
class BedrockConfig {

    @Bean
    fun bedrockRuntimeClient(credentialsProvider: AwsCredentialsProvider, region: Region): BedrockRuntimeClient {

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
            .credentialsProvider(credentialsProvider)
            .region(region)
            .build()
    }



}
