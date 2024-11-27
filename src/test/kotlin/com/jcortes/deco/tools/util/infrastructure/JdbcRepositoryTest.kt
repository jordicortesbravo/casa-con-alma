package com.jcortes.deco.tools.util.infrastructure

import com.jcortes.deco.article.infrastructure.jdbc.JdbcArticleRepository
import com.jcortes.deco.image.infrastructure.jdbc.JdbcImageRepository
import com.jcortes.deco.scrapeddocument.infrastructure.JdbcScrapedDocumentRepository
import com.jcortes.deco.tools.util.jdbc.IdRepository
import org.flywaydb.test.annotation.FlywayTest
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers
import javax.sql.DataSource

@FlywayTest
@DataJdbcTest
@Testcontainers
@ActiveProfiles("test")
@EnableJdbcRepositories(basePackageClasses = [JdbcScrapedDocumentRepository::class, JdbcImageRepository::class, JdbcArticleRepository::class, IdRepository::class])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JdbcTemplateConfig::class)
@ComponentScan(basePackageClasses = [JdbcScrapedDocumentRepository::class, JdbcImageRepository::class, JdbcArticleRepository::class, IdRepository::class])
annotation class JdbcRepositoryTest


@Configuration
class JdbcTemplateConfig {

    @Bean
    fun jdbcTemplate(dataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(dataSource)
    }
}