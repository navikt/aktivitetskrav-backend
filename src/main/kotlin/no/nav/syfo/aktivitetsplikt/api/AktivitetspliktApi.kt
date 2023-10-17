package no.nav.syfo.aktivitetsplikt.api

import jakarta.annotation.PostConstruct
import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.TokenValidator
import no.nav.syfo.aktivitetsplikt.model.Aktivitetsplikt
import no.nav.syfo.aktivitetsplikt.model.AktivitetspliktStatus
import no.nav.syfo.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.time.LocalDateTime

@Controller
@RequestMapping("/api/v1")
class AktivitetspliktApi(
    @Value("\${ESYFO_PROXY_CLIENT_ID}")
    val aktivitetskravMikrofrontendClientId: String,
    val tokenValidationContextHolder: TokenValidationContextHolder
) {
    lateinit var tokenValidator: TokenValidator
    private val log = logger()

    @PostConstruct
    fun init() {
        tokenValidator = TokenValidator(tokenValidationContextHolder, aktivitetskravMikrofrontendClientId)
    }

    @GetMapping("/aktivitetsplikt", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    @ProtectedWithClaims(issuer = "tokenx", combineWithOr = true, claimMap = ["acr=Level4", "acr=idporten-loa-high"])
    fun getAktivitetsplikt(): Aktivitetsplikt {
        log.info("Debug: Called /aktivitetsplikt")
        val claims = tokenValidator.validerTokenXClaims()
        val fnr = tokenValidator.fnrFraIdportenTokenX(claims)
        println(fnr)
//        val getAktivitetspliktFromDB =

        val dummyResponse = Aktivitetsplikt(
            status = AktivitetspliktStatus.FORHANDSVARSEL,
            arsaker = null,
            fristDato = LocalDateTime.now().plusDays(14),
            sistVurdert = LocalDateTime.now().minusDays(4),
            journalpostId = "123-456"
        )

        return dummyResponse
    }
}
