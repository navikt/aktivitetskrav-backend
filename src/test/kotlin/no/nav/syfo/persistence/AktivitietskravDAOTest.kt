package no.nav.syfo.persistence

import no.nav.syfo.LocalApplication
import no.nav.syfo.testutil.EmbeddedDatabase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext
class AktivitietskravDAOTest @Autowired constructor(
    private val jdbcTemplate: JdbcTemplate,
    private val aktivitetskravDAO: AktivitetskravDAO
) {
    private lateinit var database: EmbeddedDatabase

    @Test
    fun testInsert() {
        println("Test")
    }
}
