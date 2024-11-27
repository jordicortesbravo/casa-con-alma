package com.jcortes.deco.app

import com.jcortes.deco.article.infrastructure.jdbc.JdbcArticleRepository
import com.jcortes.deco.image.infrastructure.jdbc.JdbcImageRepository
import com.jcortes.deco.scrapeddocument.infrastructure.JdbcScrapedDocumentRepository
import com.jcortes.deco.tools.util.jdbc.IdRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import javax.sql.DataSource


@Configuration
@EnableJdbcRepositories(basePackageClasses = [JdbcScrapedDocumentRepository::class, JdbcImageRepository::class, JdbcArticleRepository::class, IdRepository::class])
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