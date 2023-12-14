package no.nav.syfo.api

import jakarta.annotation.PostConstruct
import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.TokenValidator
import no.nav.syfo.api.dto.Aktivitetsplikt
import no.nav.syfo.exception.LogLevel
import no.nav.syfo.exception.ResourceNotFoundException
import no.nav.syfo.logger
import no.nav.syfo.service.AktivitetskravService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Profile("remote")
@Controller
@RequestMapping("/api/v1")
class AktivitetspliktApi(
    @Value("\${ESYFO_PROXY_CLIENT_ID}")
    val aktivitetskravMikrofrontendClientId: String,
    val tokenValidationContextHolder: TokenValidationContextHolder,
    val aktivitetskravService: AktivitetskravService
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
    fun getAktivitetsplikt(): Aktivitetsplikt? {
        val claims = tokenValidator.validerTokenXClaims()
        val fnr = tokenValidator.fnrFraIdportenTokenX(claims)

        return aktivitetskravService.getAktivitetsplikt(fnr) ?: throw ResourceNotFoundException(
            message = "Ingen aktivitetskrav funnet",
            httpStatus = HttpStatus.NOT_FOUND,
            reason = "Ingen aktivitetskrav funnet",
            loglevel = LogLevel.OFF
        )
    }

    @PostMapping("/aktivitetsplikt/les")
    @ResponseBody
    @ProtectedWithClaims(issuer = "tokenx", combineWithOr = true, claimMap = ["acr=Level4", "acr=idporten-loa-high"])
    fun postLesHendelse(): HttpStatus {
        val claims = tokenValidator.validerTokenXClaims()
        val fnr = tokenValidator.fnrFraIdportenTokenX(claims)

        try {
            aktivitetskravService.sendFerdigstillToVarselbus(fnr)
            log.info("Sent les event to varselbus: OK")
            return HttpStatus.OK
        } catch (e: Exception) {
            log.error("Sent les event to varselbus failed due to exception")
            return HttpStatus.INTERNAL_SERVER_ERROR
        }
    }

    @GetMapping("/aktivitetsplikt/historikk", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    @ProtectedWithClaims(issuer = "tokenx", combineWithOr = true, claimMap = ["acr=Level4", "acr=idporten-loa-high"])
    fun getAktivitetspliktHistorikk(): List<Aktivitetsplikt>? {
        val claims = tokenValidator.validerTokenXClaims()
        val fnr = tokenValidator.fnrFraIdportenTokenX(claims)

        return aktivitetskravService.getAktivitetspliktHistorikk(fnr) ?: throw ResourceNotFoundException(
            message = "Ingen aktivitetskrav funnet",
            httpStatus = HttpStatus.NOT_FOUND,
            reason = "Ingen aktivitetskrav funnet",
            loglevel = LogLevel.OFF
        )
    }
}
