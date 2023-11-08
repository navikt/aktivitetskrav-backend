package no.nav.syfo.tokenX

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.security.token.support.core.jwt.JwtTokenClaims
import no.nav.syfo.exception.AbstractApiError
import no.nav.syfo.exception.LogLevel
import org.springframework.http.HttpStatus

object TokenXUtil {
    @Throws(IngenTilgang::class)
    fun validateTokenXClaims(
        contextHolder: TokenValidationContextHolder,
        vararg requestedClientId: String,
    ): JwtTokenClaims {
        val context = contextHolder.tokenValidationContext
        val claims = context.getClaims(TokenXIssuer.TOKENX)
        val clientId = claims.getStringClaim("client_id")

        if (!requestedClientId.toList().contains(clientId)) {
            throw IngenTilgang("Uventet client id $clientId")
        }
        return claims
    }

    fun JwtTokenClaims.fnrFromIdportenTokenX(): String {
        return this.getStringClaim("pid")
    }

    fun fnrFromIdportenTokenX(contextHolder: TokenValidationContextHolder): String {
        val context = contextHolder.tokenValidationContext
        val claims = context.getClaims(TokenXIssuer.TOKENX)
        return claims.getStringClaim("pid")
    }

    fun tokenFromTokenX(contextHolder: TokenValidationContextHolder): String {
        val context = contextHolder.tokenValidationContext
        return context.getJwtToken(TokenXIssuer.TOKENX).tokenAsString
    }

    object TokenXIssuer {
        const val TOKENX = "tokenx"
    }
}

class IngenTilgang(override val message: String) : AbstractApiError(
    message = message,
    httpStatus = HttpStatus.FORBIDDEN,
    reason = "INGEN_TILGANG",
    loglevel = LogLevel.WARN
)
