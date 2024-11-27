package com.jcortes.deco.tools.util.bedrock

interface BedrockFoundationModel {
    val id: String
}

enum class BedrockTextModel(override val id: String) : BedrockFoundationModel {
    CLAUDE_INSTANT("anthropic.claude-instant-v1"),
    CLAUDE_HAIKU_3("anthropic.claude-3-haiku-20240307-v1:0"),
    CLAUDE_SONNET_3_5("anthropic.claude-3-5-sonnet-20240620-v1:0")
}

enum class BedrockEmbeddingModel(override val id: String) : BedrockFoundationModel {
    COHERE_EMBED_MULTILINGUAL_V3("cohere.embed-multilingual-v3")
}

enum class BedrockImageModel(override val id: String) : BedrockFoundationModel {
    STABLE_SDXL("stability.stable-diffusion-xl-v1"),
    STABLE_IMAGE_CORE("stability.stable-image-core-v1:0"),
    STABLE_IMAGE_ULTRA("stability.stable-image-ultra-v1:0")
}