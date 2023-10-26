package no.nav.syfo.testutil

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import java.sql.Connection

class EmbeddedDatabase {
    private lateinit var pg: EmbeddedPostgres

    val hikariDataSource = HikariDataSource(
        HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://localhost:5432/postgres"
            username = "postgres"
            password = "postgres"
            maximumPoolSize = 3
            minimumIdle = 1
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_READ_COMMITTED"
            validate()
        }
    )

    init {
        pg = EmbeddedPostgres.start()

        Flyway.configure().run {
            dataSource(hikariDataSource).load().migrate()
        }
    }

    fun stop() {
        pg.close()
    }
}

fun Connection.dropData() {
    val query1 = "DELETE FROM AKTIVITETSKRAV_VARSEL"
    val query2 = "DELETE FROM AKTIVITETSKRAV_VURDERING"

    use { connection ->
        connection.prepareStatement(query1).executeUpdate()
        connection.prepareStatement(query2).executeUpdate()
        connection.commit()
    }
}
