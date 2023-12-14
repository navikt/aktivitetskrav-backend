package no.nav.syfo.persistence

import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import no.nav.syfo.LocalApplication
import no.nav.syfo.api.dto.AktivitetspliktStatus
import no.nav.syfo.service.domain.AktivitetskravVurdering
import org.junit.jupiter.api.AfterEach
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
class AktivitietskravDAOTest {

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var aktivitetskravDAO: AktivitetskravDAO

    @AfterEach
    fun cleanup() {
        jdbcTemplate.update("DELETE FROM AKTIVITETSKRAV_VURDERING")
        jdbcTemplate.update("DELETE FROM AKTIVITETSKRAV_VARSEL")
    }

    @Test
    fun testInsertAndFetch() {
        var nrUpdated = aktivitetskravDAO.storeAktivitetkravVurdering(
            generateKAktivitetkravVurdering(personIdent = FNR_1, status = AktivitetspliktStatus.NY)
        )
        nrUpdated += aktivitetskravDAO.storeAktivitetkravVurdering(
            generateKAktivitetkravVurdering(personIdent = FNR_1, status = AktivitetspliktStatus.AVVENT)
        )
        nrUpdated += aktivitetskravDAO.storeAktivitetkravVurdering(
            generateKAktivitetkravVurdering(personIdent = FNR_2, status = AktivitetspliktStatus.NY)
        )
        nrUpdated shouldBe 3

        aktivitetskravDAO
            .fetchAktivitetkravVurderingByIdent(FNR_1)
            .forAll { it.personIdent shouldBe FNR_1 }
    }

    @Test
    fun testOnlyFetchLastAktivitetskrav() {
        aktivitetskravDAO.storeAktivitetkravVurdering(
            generateKAktivitetkravVurdering(personIdent = FNR_1, status = AktivitetspliktStatus.NY)
        )
        aktivitetskravDAO.storeAktivitetkravVurdering(
            generateKAktivitetkravVurdering(personIdent = FNR_1, status = AktivitetspliktStatus.AVVENT)
        )

        aktivitetskravDAO.storeAktivitetkravVurdering(
            generateKAktivitetkravVurdering(personIdent = FNR_1, status = AktivitetspliktStatus.NY_VURDERING, uuid = "ee3f5b44-b6e3-4220-9afc-f8fc1f627c85")
        )
        aktivitetskravDAO.storeAktivitetkravVurdering(
            generateKAktivitetkravVurdering(personIdent = FNR_1, status = AktivitetspliktStatus.IKKE_AKTUELL, uuid = "ee3f5b44-b6e3-4220-9afc-f8fc1f627c85")
        )
        aktivitetskravDAO.fetchAktivitetkravVurderingByIdent(FNR_1).size.shouldBeExactly(4)

        val fetchedHistoriskAktivitetsplikt = aktivitetskravDAO.getHistoriskAktivitetsplikt(FNR_1)

        fetchedHistoriskAktivitetsplikt?.size?.shouldBeExactly(2)
        fetchedHistoriskAktivitetsplikt?.get(0)?.status?.shouldBeIn(listOf(AktivitetspliktStatus.NY_VURDERING, AktivitetspliktStatus.IKKE_AKTUELL))
        fetchedHistoriskAktivitetsplikt?.get(1)?.status?.shouldBeIn(listOf(AktivitetspliktStatus.NY_VURDERING, AktivitetspliktStatus.IKKE_AKTUELL))
    }

    @Test
    fun testFetchVurderingerOgVarsler() {
        val VURDERING_UUID_2 = "ee3f5b44-b6e3-4220-9afc-f8fc1f627c85"
        aktivitetskravDAO.storeAktivitetkravVurdering(
            generateKAktivitetkravVurdering(personIdent = FNR_1, status = AktivitetspliktStatus.NY)
        )
        aktivitetskravDAO.storeAktivitetkravVurdering(
            generateKAktivitetkravVurdering(personIdent = FNR_2, status = AktivitetspliktStatus.NY)
        )
        aktivitetskravDAO.storeAktivitetkravVurdering(
            generateKAktivitetkravVurdering(personIdent = FNR_1, status = AktivitetspliktStatus.AVVENT)
        )
        aktivitetskravDAO.storeAktivitetkravVurdering(
            generateKAktivitetkravVurdering(personIdent = FNR_1, status = AktivitetspliktStatus.NY_VURDERING, uuid = VURDERING_UUID_2)
        )
        aktivitetskravDAO.storeAktivitetkravVurdering(
            generateKAktivitetkravVurdering(personIdent = FNR_1, status = AktivitetspliktStatus.FORHANDSVARSEL, uuid = VURDERING_UUID_2)
        )
        aktivitetskravDAO.storeAktivitetkravVarsel(generateKAktivitetkravVarsel(personIdent = FNR_1, aktivitetskravUuid = VURDERING_UUID_2))

        val fetchedHistoriskAktivitetsplikt = aktivitetskravDAO.getHistoriskAktivitetsplikt(FNR_1)

        fetchedHistoriskAktivitetsplikt?.size?.shouldBeExactly(2)
        fetchedHistoriskAktivitetsplikt?.get(0)?.status?.shouldBeIn(listOf(AktivitetspliktStatus.NY_VURDERING, AktivitetspliktStatus.FORHANDSVARSEL))
        fetchedHistoriskAktivitetsplikt?.get(1)?.status?.shouldBeIn(listOf(AktivitetspliktStatus.NY_VURDERING, AktivitetspliktStatus.FORHANDSVARSEL))
    }
}

fun AktivitetskravDAO.fetchAktivitetkravVurderingByIdent(personIdent: String): List<AktivitetskravVurdering> {
    return jdbcTemplate.query("SELECT * FROM AKTIVITETSKRAV_VURDERING WHERE person_ident = ?", AktivitetskravDAO.aktivitetskravVurderingRowMapper, personIdent)
}
