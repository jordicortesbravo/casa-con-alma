package com.jcortes.deco.article.infrastructure.bedrock

import com.jcortes.deco.tools.util.bedrock.BedrockTextModel

class BedrockTextInferenceRequest {
    lateinit var model: BedrockTextModel
    lateinit var userPrompt: String
    var systemPrompt: String? = null
    var temperature: Float = 0.5f
    var maxTokens: Int = 2048
    var topP: Float = 1.0f
}