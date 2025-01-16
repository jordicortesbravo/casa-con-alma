package com.jcortes.deco.image.infrastructure.bedrock

import com.jcortes.deco.tools.util.bedrock.BedrockImageModel

class BedrockImageInferenceRequest {
    lateinit var model: BedrockImageModel
    lateinit var userPrompt: String
    var negativePrompt: String? = null
    var image: ByteArray? = null
}