package no.nav.syfo.api

import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.syfo.api.dto.Aktivitetsplikt
import no.nav.syfo.exception.LogLevel
import no.nav.syfo.exception.ResourceNotFoundException
import no.nav.syfo.service.AktivitetskravService
import no.nav.syfo.tokenX.TokenXUtil
import no.nav.syfo.tokenX.TokenXUtil.fnrFromIdportenTokenX
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Profile("remote")
@Controller
@RequestMapping("/api/v1")
class AktivitetspliktApi(
    @Value("\${ESYFO_PROXY_CLIENT_ID}")
    val esyfoProxyClientId: String,
    @Value("\${AKTIVITETSKRAV_FRONTEND_CLIENT_ID}")
    val aktivitetskravFrontendClientId: String,
    val tokenValidationContextHolder: TokenValidationContextHolder,
    val aktivitetskravService: AktivitetskravService
) {
    @GetMapping("/aktivitetsplikt", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    @ProtectedWithClaims(issuer = "tokenx", combineWithOr = true, claimMap = ["acr=Level4", "acr=idporten-loa-high"])
    fun getAktivitetsplikt(): Aktivitetsplikt? {
        val claims = TokenXUtil.validateTokenXClaims(
            tokenValidationContextHolder,
            esyfoProxyClientId,
            aktivitetskravFrontendClientId
        )

        return aktivitetskravService.getAktivitetsplikt(claims.fnrFromIdportenTokenX())
            ?: throw ResourceNotFoundException(
                message = "Ingen aktivitetskrav funnet",
                httpStatus = HttpStatus.NOT_FOUND,
                reason = "Ingen aktivitetskrav funnet",
                loglevel = LogLevel.OFF
            )
    }
}
