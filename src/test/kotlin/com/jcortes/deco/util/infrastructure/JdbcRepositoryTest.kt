package com.jcortes.deco.util.infrastructure

import com.jcortes.deco.content.JdbcScrapedDocumentRepository
import com.jcortes.deco.util.IdGenerator
import com.jcortes.deco.util.SnowflakeIdGenerator
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

@DataJdbcTest
@Testcontainers
@ActiveProfiles("test")
@EnableJdbcRepositories(basePackageClasses = [JdbcScrapedDocumentRepository::class])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@FlywayTest
@Import(JdbcTemplateConfig::class)
@ComponentScan(basePackageClasses = [JdbcScrapedDocumentRepository::class])
annotation class JdbcRepositoryTest


@Configuration
class JdbcTemplateConfig {
    @Bean
    fun jdbcTemplate(dataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(dataSource)
    }

    @Bean
    fun idGenerator(): IdGenerator {
        return SnowflakeIdGenerator()
    }
}