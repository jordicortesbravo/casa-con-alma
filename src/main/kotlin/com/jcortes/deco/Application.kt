package com.jcortes.deco

import org.slf4j.LoggerFactory
import org.springframework.boot.Banner
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.env.EnvironmentEndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.health.HealthEndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.info.InfoContributorAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.info.InfoEndpointAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.metrics.*
import org.springframework.boot.actuate.autoconfigure.observation.ObservationAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.observation.web.servlet.WebMvcObservationAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.web.reactive.ReactiveManagementContextAutoConfiguration
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.context.TypeExcludeFilter
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType

@SpringBootConfiguration
@ImportAutoConfiguration(
    classes = [
        // Core
        WebMvcAutoConfiguration::class,
        DispatcherServletAutoConfiguration::class,
        ServletWebServerFactoryAutoConfiguration::class,
        ErrorMvcAutoConfiguration::class,
        HttpMessageConvertersAutoConfiguration::class,

        // Props
        ConfigurationPropertiesAutoConfiguration::class,
        PropertyPlaceholderAutoConfiguration::class,

        // ObjectMapper
        JacksonAutoConfiguration::class,

        //DDBB
        FlywayAutoConfiguration::class,
        SqlInitializationAutoConfiguration::class,
        DataSourceAutoConfiguration::class,
        JdbcRepositoriesAutoConfiguration::class,

        // Tasks
        TaskExecutionAutoConfiguration::class,
        TaskSchedulingAutoConfiguration::class,

        // Actuators
        EndpointAutoConfiguration::class,
        WebEndpointAutoConfiguration::class,
        ManagementContextAutoConfiguration::class,
        ReactiveManagementContextAutoConfiguration::class,
        ProjectInfoAutoConfiguration::class,
        InfoContributorAutoConfiguration::class,
        InfoEndpointAutoConfiguration::class,
        HealthEndpointAutoConfiguration::class,
        EnvironmentEndpointAutoConfiguration::class,
        MetricsEndpointAutoConfiguration::class,
    ]
)
@ComponentScan(
    basePackageClasses = [Application::class],
    excludeFilters = [
        ComponentScan.Filter(type = FilterType.CUSTOM, classes = [TypeExcludeFilter::class]),
        ComponentScan.Filter(type = FilterType.CUSTOM, classes = [AutoConfigurationExcludeFilter::class])
    ]
)
class Application

fun main(args: Array<String>) {
    val log = LoggerFactory.getLogger(Application::class.java)
    runApplication<Application>(*args) {
        setBannerMode(Banner.Mode.OFF)
        setAdditionalProfiles("config")
    }
    log.info("Application initialized")
}