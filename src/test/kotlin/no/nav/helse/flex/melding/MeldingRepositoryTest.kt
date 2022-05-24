package no.nav.helse.flex.melding

import no.nav.helse.flex.FellesTestOppsett
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

class MeldingRepositoryTest : FellesTestOppsett() {

    @Test
    fun testReposistory() {
        val melding = MeldingDbRecord(
            fnr = "12345",
            opprettet = Instant.EPOCH,
            lukket = null,
            tekst = "Heyyy",
            url = "http://heisann",
            meldingUuid = UUID.randomUUID().toString()
        )
        meldingRepository.save(melding)

        meldingRepository.count() `should be equal to` 1

        meldingRepository.findByFnrIn(listOf("12345")).first().tekst `should be equal to` "Heyyy"
    }
}