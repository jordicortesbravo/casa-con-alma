package com.jcortes.deco.content

import com.jcortes.deco.client.bedrock.BedrockTextClient
import com.jcortes.deco.content.model.Article
import com.jcortes.deco.content.model.Image
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import kotlin.test.assertContentEquals

class ArticleServiceTest {

    private val bedrockTextClient = mock<BedrockTextClient>()
    private val articleRepository = mock<ArticleRepository>()
    private val imageService = mock<ImageService>()

    private val articleService = ArticleService(bedrockTextClient, articleRepository, imageService)

    @Test
    fun generateAndAddImages() {
        val article = Article().apply {
            title = "title"
            subtitle = "subtitle"
            content = """
                <p>content</p>
                <img caption="caption 1" data-ia-prompt="prompt 1"></img>
                <p>content</p>
                <img caption="caption 2" data-ia-prompt="prompt 2"/>
                <p>content</p>
                <img data-ia-prompt="prompt 3" caption="caption 3"/>
            """.trimIndent()
        }
        val image1 = Image().apply {
            seoUrl = "seoUrl1"
            caption = "caption 1"
        }
        val image2 = Image().apply {
            seoUrl = "seoUrl2"
            caption = "caption 2"
        }
        val image3 = Image().apply {
            seoUrl = "seoUrl3"
            caption = "caption 3"
        }
        val images = listOf(image1, image2, image3)

        `when`(imageService.generate("prompt 1")).thenReturn(image1)
        `when`(imageService.generate("prompt 2")).thenReturn(image2)
        `when`(imageService.generate("prompt 3")).thenReturn(image3)

        articleService.generateAndAddImages(article)

        assertThat(article.content).isEqualTo(
            """
                <p>content</p>
                <img src="images/seoUrl1" caption="caption 1"/>
                <p>content</p>
                <img src="images/seoUrl2" caption="caption 2"/>
                <p>content</p>
                <img src="images/seoUrl3" caption="caption 3"/>
            """.trimIndent()
        )
        assertContentEquals(images, article.images)
    }

}