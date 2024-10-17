package com.jcortes.deco.content

import com.jcortes.deco.content.infrastructure.JdbcImageRepository
import com.jcortes.deco.content.model.Image
import com.jcortes.deco.util.infrastructure.JdbcRepositoryTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.init.ScriptUtils
import org.springframework.transaction.annotation.Transactional
import javax.sql.DataSource
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull

@JdbcRepositoryTest
class JdbcImageRepositoryTest @Autowired constructor(
    private val repository: JdbcImageRepository,
    private val dataSource: DataSource,
) {

    @BeforeEach
    fun setup() {
        dataSource.connection.use { connection ->
            ScriptUtils.executeSqlScript(connection, ClassPathResource("db/clean.sql"))
            ScriptUtils.executeSqlScript(connection, ClassPathResource("db/import.sql"))
        }
    }

    @Test
    fun `it should get an image by id`() {
        val image = repository.get(1)!!
        assertEquals(image.id, 1)
        assertEquals(image.sourceId, "t1")
        assertThat(image.description).isNotBlank()
        assertThat(image.sourceUrl.toString()).isEqualTo("https://example.com/image1.jpg")
        assertContentEquals(image.keywords, listOf("decor", "living room"))
        assertContentEquals(image.keywords, listOf("decor", "living room"))
    }

    @Test
    fun `it should get an image by source id`() {
        val image = repository.get("t2")!!
        assertEquals(image.id, 2)
        assertEquals(image.sourceId, "t2")
        assertThat(image.description).isNotBlank()
        assertThat(image.sourceUrl.toString()).isEqualTo("https://example.com/image2.jpg")
        assertContentEquals(image.keywords, listOf("bathroom", "decor"))
    }

    @Test
    fun `it should get all images by ids`() {
        val images = repository.list(listOf(1, 2))
        assertEquals(images.size, 2)
        assertEquals(images[0].id, 1)
        assertEquals(images[1].id, 2)
    }

    @Test
    @Transactional
    fun `it should save an image`() {

        assertNull(repository.get(10))

        val image = Image().apply {
            id = 10
            sourceId = "t10"

            keywords = listOf("decor", "living room")
            description = "Image 10"
            embedding = (0 until 1024).map { it.toFloat() }
            author = "Author 10"

            sourceUrl = "https://example.com/image10.jpg"
            url = "https://example.com/image10.jpg"
            seoUrl = "https://example.com/image10.jpg"
            internalUri = "file:///tmp/image10.jpg"
        }
        repository.save(image)

        val savedImage = repository.get(10)!!
        assertEquals(savedImage.id, savedImage.id)
        assertEquals(savedImage.sourceId, savedImage.sourceId)
        assertContentEquals(savedImage.keywords, savedImage.keywords)
        assertEquals(savedImage.description, savedImage.description)
        assertNull(savedImage.embedding)
        assertEquals(savedImage.author, savedImage.author)
        assertEquals(savedImage.sourceUrl, savedImage.sourceUrl)
        assertEquals(savedImage.url, savedImage.url)
        assertEquals(savedImage.seoUrl, savedImage.seoUrl)
        assertEquals(savedImage.internalUri, savedImage.internalUri)
    }
}