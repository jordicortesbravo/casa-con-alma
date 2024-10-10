package com.jcortes.deco.content

import com.jcortes.deco.content.infrastructure.JdbcScrapedDocumentRepository
import com.jcortes.deco.content.model.ScrapedDocument
import com.jcortes.deco.content.model.SiteCategory
import com.jcortes.deco.util.infrastructure.JdbcRepositoryTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.init.ScriptUtils
import java.net.URI
import javax.sql.DataSource
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull

@JdbcRepositoryTest
class JdbcScrapedArticleRepositoryTest @Autowired constructor(
    private val repository: JdbcScrapedDocumentRepository,
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
    fun `it should get a scraped document by id`() {
        val document = repository.get(1)!!
        assertEquals(document.id, 1)
        assertEquals(document.sourceId, "18306")
        assertThat(document.title).isNotBlank()
        assertThat(document.subtitle).isNotBlank()
        assertThat(document.content).isNotBlank()
        assertContentEquals(document.keywords, listOf("salon", "pequeño"))
        assertContentEquals(document.siteCategories, listOf(SiteCategory.LIVING_AND_DINING_ROOMS))
        assertContentEquals(document.productCategories, listOf("comedor"))
    }

    @Test
    fun `it should get a scraped document by source id`() {
        val document = repository.get("2097")!!
        assertEquals(document.id, 2)
        assertEquals(document.sourceId, "2097")
        assertThat(document.title).isNotBlank()
        assertThat(document.subtitle).isNotBlank()
        assertThat(document.content).isNotBlank()
        assertContentEquals(document.keywords, listOf("plantas", "jardín", "peonía"))
        assertContentEquals(document.siteCategories, listOf(SiteCategory.KITCHENS))
        assertContentEquals(document.productCategories, listOf("almacenamiento-cocina", "cocina"))
    }

    @Test
    fun `it should get all scraped documents by ids`() {
        val documents = repository.list(listOf(1, 2))
        assertEquals(documents.size, 2)
        assertEquals(documents[0].id, 1)
        assertEquals(documents[1].id, 2)
    }

    @Test
    fun `it should save a scraped document`() {

        assertNull(repository.get(10))

        val scrapedDocument = ScrapedDocument().apply {
            id = 10
            url = URI("https://fake-url.com")
            sourceId = "12345"
            title = "Title"
            subtitle = "Subtitle"
            content = "Content"
            keywords = listOf("keyword1", "keyword2")
            siteCategories = listOf(SiteCategory.BATHROOMS)
            productCategories = listOf("bathroom")
        }
        repository.save(scrapedDocument)

        val savedDocument = repository.get("12345")!!
        assertEquals(savedDocument.id, scrapedDocument.id)
        assertEquals(savedDocument.sourceId, scrapedDocument.sourceId)
        assertEquals(savedDocument.title, scrapedDocument.title)
        assertEquals(savedDocument.subtitle, scrapedDocument.subtitle)
        assertEquals(savedDocument.content, scrapedDocument.content)
        assertContentEquals(savedDocument.keywords, scrapedDocument.keywords)
        assertContentEquals(savedDocument.siteCategories, scrapedDocument.siteCategories)
        assertContentEquals(savedDocument.productCategories, scrapedDocument.productCategories)
    }

}