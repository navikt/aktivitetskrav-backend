package no.nav.syfo.persistence

import io.kotest.inspectors.forAll
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import no.nav.syfo.LocalApplication
import no.nav.syfo.service.domain.AktivitetskravVurdering
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.jdbc.JdbcTestUtils

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [LocalApplication::class])
@DirtiesContext
class AktivitietskravDAOTest @Autowired constructor(
    private val aktivitetskravDAO: AktivitetskravDAO,
) {

    @AfterEach
    fun tearDown() {
        JdbcTestUtils.deleteFromTables(aktivitetskravDAO.jdbcTemplate, "AKTIVITETSKRAV_VURDERING")
    }

    @Test
    fun testInsertAndFetch() {
        var nrUpdated = aktivitetskravDAO.storeAktivitetkravVurdering(
            generateKAktivitetkravVurdering(personIdent = FNR_1),
        )
        nrUpdated += aktivitetskravDAO.storeAktivitetkravVurdering(
            generateKAktivitetkravVurdering(personIdent = FNR_1),
        )
        nrUpdated += aktivitetskravDAO.storeAktivitetkravVurdering(
            generateKAktivitetkravVurdering(personIdent = FNR_2),
        )
        nrUpdated shouldBe 3

        aktivitetskravDAO
            .fetchAktivitetkravVurderingByIdent(FNR_1)
            .forAll { it.personIdent shouldBe FNR_1 }
    }

    @Test
    fun testList() {
        var nrUpdated = aktivitetskravDAO.storeAktivitetkravVurdering(
            generateKAktivitetkravVurdering(personIdent = FNR_1),
        )
        nrUpdated += aktivitetskravDAO.storeAktivitetkravVurdering(
            generateKAktivitetkravVurdering(personIdent = FNR_1),
        )
        nrUpdated += aktivitetskravDAO.storeAktivitetkravVurdering(
            generateKAktivitetkravVurdering(personIdent = FNR_2),
        )
        nrUpdated shouldBe 3

        aktivitetskravDAO.getHistoriskAktivitetsplikt(FNR_1)?.size?.shouldBeExactly(2)
    }
}

fun AktivitetskravDAO.fetchAktivitetkravVurderingByIdent(personIdent: String): List<AktivitetskravVurdering> {
    return jdbcTemplate.query("SELECT * FROM aktivitetskrav_vurdering WHERE person_ident = ?", AktivitetskravDAO.aktivitetskravVurderingRowMapper, personIdent)
}
