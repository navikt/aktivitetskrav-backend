package no.nav.syfo

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import no.nav.security.token.support.spring.test.MockOAuth2ServerAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@Configuration
@Import(MockOAuth2ServerAutoConfiguration::class)
class LocalApplicationConfig {

    @Bean
    fun dataSource() = embeddedPostgres().getPostgresDatabase()

    @Bean
    fun embeddedPostgres() = EmbeddedPostgres.start()

    @Bean
    fun jdbcTemplate(dataSource: DataSource) = JdbcTemplate(dataSource)
}
