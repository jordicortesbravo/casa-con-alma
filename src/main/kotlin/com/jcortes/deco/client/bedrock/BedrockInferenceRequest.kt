package com.jcortes.deco.client.bedrock

class BedrockTextInferenceRequest {
    lateinit var model: BedrockTextModel
    lateinit var userPrompt: String
    var systemPrompt: String? = null
    var temperature: Float = 0.5f
    var maxTokens: Int = 2048
    var topP: Float = 1.0f
}

class BedrockImageInferenceRequest {
    lateinit var model: BedrockImageModel
    lateinit var userPrompt: String
    var image: ByteArray? = null
}