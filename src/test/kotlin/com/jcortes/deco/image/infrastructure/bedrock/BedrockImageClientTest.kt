package com.jcortes.deco.image.infrastructure.bedrock


import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jcortes.deco.tools.util.bedrock.BedrockImageModel
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient

class BedrockImageClientTest {

    private val bedrockRuntimeClient = bedrockRuntimeClient()
    private val objectMapper = jacksonObjectMapper()

    @Test
    fun testInvokeImageModel() {
        val bedrockImageClient = BedrockImageClient(bedrockRuntimeClient, objectMapper)
        val userPrompt =
            "A beautifully styled image of an elegant Christmas table setting in a rustic yet modern style. The table is set with crisp white linens, matte black plates and cutlery, and natural elements like pine branches, berries, and candles. The centerpiece features a simple yet striking arrangement of fresh greenery and dried botanicals. The overall aesthetic is minimalist and Scandinavian-inspired, with a focus on natural textures and materials. The lighting is warm and inviting, creating a cozy and festive ambiance."
        val model = BedrockImageModel.STABLE_IMAGE_CORE
        val request = BedrockImageInferenceRequest().apply {
            this.model = model
            this.userPrompt = userPrompt
        }
        val result = bedrockImageClient.invokeStableDiffusionModel(request)
        assertNotNull(result)
    }

    private fun bedrockRuntimeClient(): BedrockRuntimeClient {
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