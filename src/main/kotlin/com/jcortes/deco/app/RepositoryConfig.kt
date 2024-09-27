package com.jcortes.deco.app

import com.jcortes.deco.content.infrastructure.JdbcScrapedDocumentRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import javax.sql.DataSource


@Configuration
@EnableJdbcRepositories(basePackageClasses = [JdbcScrapedDocumentRepository::class])
class RepositoryConfig {

    @Bean
    fun transactionManager(dataSource: DataSource): DataSourceTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }

    @Bean
    fun namedParameterJdbcTemplate(dataSource: DataSource): NamedParameterJdbcTemplate {
        return NamedParameterJdbcTemplate(dataSource)
    }

    @Bean
    fun jdbcTemplate(dataSource: DataSource): JdbcTemplate {
        return JdbcTemplate(dataSource)
    }
}