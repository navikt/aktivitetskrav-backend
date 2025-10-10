package no.nav.syfo

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.security.token.support.core.jwt.JwtTokenClaims
import no.nav.syfo.exception.AbstractApiError
import no.nav.syfo.exception.LogLevel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class TokenValidator(
    @param:Autowired
    private val tokenValidationContextHolder: TokenValidationContextHolder,
    private val expectedClientId: String
) {
    fun validerTokenXClaims(): JwtTokenClaims {
        val context = tokenValidationContextHolder.getTokenValidationContext()
        val claims = context.getClaims("tokenx")
        val clientId = claims.getStringClaim("client_id")
        if (clientId != expectedClientId) {
            throw IngenTilgang("Uventet client id $clientId")
        }
        return claims
    }

    fun fnrFraIdportenTokenX(claims: JwtTokenClaims): String = claims.getStringClaim("pid")
}

class IngenTilgang(override val message: String) :
    AbstractApiError(
        message = message,
        httpStatus = HttpStatus.FORBIDDEN,
        reason = "INGEN_TILGANG",
        loglevel = LogLevel.WARN
    )
