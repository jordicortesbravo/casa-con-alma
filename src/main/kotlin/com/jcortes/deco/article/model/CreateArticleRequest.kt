package com.jcortes.deco.article.model

import com.jcortes.deco.tools.util.bedrock.BedrockImageModel

data class CreateArticleRequest(
    val article: Article,
    val imageModel: BedrockImageModel? = null,
    val systemPrompt: String? = null
)