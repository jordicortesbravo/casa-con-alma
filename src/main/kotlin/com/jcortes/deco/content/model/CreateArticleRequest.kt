package com.jcortes.deco.content.model

import com.jcortes.deco.client.bedrock.BedrockImageModel

data class CreateArticleRequest(
    val article: Article,
    val imageModel: BedrockImageModel? = null,
    val systemPrompt: String? = null
)