package com.jcortes.deco.app

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.regions.Region

@Configuration
class AwsConfig {

    companion object {
        private const val PROFILE_NAME = "YE-Playground"
    }

    @Bean
    fun ssoProfileCredentialsProvider(): AwsCredentialsProvider {
        return ProfileCredentialsProvider.create(PROFILE_NAME)
    }

    @Bean
    fun ssoProfileRegion(): Region {
        return Region.of("us-west-2")
    }
}