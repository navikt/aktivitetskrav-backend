package no.nav.syfo.persistence

import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import no.nav.syfo.LocalApplication
import no.nav.syfo.service.domain.AktivitetskravVurdering
import no.nav.syfo.testutil.EmbeddedDatabase
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext
class AktivitietskravDAOTest @Autowired constructor(
    private val aktivitetskravDAO: AktivitetskravDAO
) {
    private lateinit var database: EmbeddedDatabase

    @Test
    fun testInsertAndFetch() {
        var nrUpdated = aktivitetskravDAO.storeAktivitetkravVurdering(
            generateKAktivitetkravVurdering(personIdent = FNR_1)
        )
        nrUpdated += aktivitetskravDAO.storeAktivitetkravVurdering(
            generateKAktivitetkravVurdering(personIdent = FNR_1)
        )
        nrUpdated += aktivitetskravDAO.storeAktivitetkravVurdering(
            generateKAktivitetkravVurdering(personIdent = FNR_2)
        )
        nrUpdated shouldBe 3

        aktivitetskravDAO
            .fetchAktivitetkravVurderingByIdent(FNR_1)
            .forAll { it.personIdent shouldBe FNR_1 }
    }
}

fun AktivitetskravDAO.fetchAktivitetkravVurderingByIdent(personIdent: String): List<AktivitetskravVurdering> {
    return jdbcTemplate.query("SELECT * FROM aktivitetskrav_vurdering WHERE person_ident = ?", AktivitetskravDAO.aktivitetskravVurderingRowMapper, personIdent)
}
