package no.nav.syfo

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.security.token.support.core.context.TokenValidationContext
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import no.nav.security.token.support.core.jwt.JwtTokenClaims
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TokenValidatorTest {

    private val tokenValidationContextHolder = mockk<TokenValidationContextHolder>()
    private val tokenValidationContext = mockk<TokenValidationContext>()
    private val jwtTokenClaims = mockk<JwtTokenClaims>()

    @Test
    fun `should accept token with first expected client id`() {
        val expectedClientIds = setOf("dev-gcp:team-esyfo:esyfo-proxy", "dev-gcp:team-esyfo:aktivitetskrav-frontend")
        val tokenValidator = TokenValidator(tokenValidationContextHolder, expectedClientIds)

        every { tokenValidationContextHolder.getTokenValidationContext() } returns tokenValidationContext
        every { tokenValidationContext.getClaims("tokenx") } returns jwtTokenClaims
        every { jwtTokenClaims.getStringClaim("client_id") } returns "dev-gcp:team-esyfo:esyfo-proxy"

        val claims = tokenValidator.validerTokenXClaims()

        claims shouldBe jwtTokenClaims
    }

    @Test
    fun `should accept token with second expected client id`() {
        val expectedClientIds = setOf("dev-gcp:team-esyfo:esyfo-proxy", "dev-gcp:team-esyfo:aktivitetskrav-frontend")
        val tokenValidator = TokenValidator(tokenValidationContextHolder, expectedClientIds)

        every { tokenValidationContextHolder.getTokenValidationContext() } returns tokenValidationContext
        every { tokenValidationContext.getClaims("tokenx") } returns jwtTokenClaims
        every { jwtTokenClaims.getStringClaim("client_id") } returns "dev-gcp:team-esyfo:aktivitetskrav-frontend"

        val claims = tokenValidator.validerTokenXClaims()

        claims shouldBe jwtTokenClaims
    }

    @Test
    fun `should reject token with unexpected client id`() {
        val expectedClientIds = setOf("dev-gcp:team-esyfo:esyfo-proxy", "dev-gcp:team-esyfo:aktivitetskrav-frontend")
        val tokenValidator = TokenValidator(tokenValidationContextHolder, expectedClientIds)

        every { tokenValidationContextHolder.getTokenValidationContext() } returns tokenValidationContext
        every { tokenValidationContext.getClaims("tokenx") } returns jwtTokenClaims
        every { jwtTokenClaims.getStringClaim("client_id") } returns "dev-gcp:team-esyfo:unauthorized-app"

        val exception = assertThrows<IngenTilgang> {
            tokenValidator.validerTokenXClaims()
        }

        exception.message shouldBe "Uventet client id dev-gcp:team-esyfo:unauthorized-app"
    }

    @Test
    fun `should reject token with null client_id claim`() {
        val expectedClientIds = setOf("dev-gcp:team-esyfo:esyfo-proxy", "dev-gcp:team-esyfo:aktivitetskrav-frontend")
        val tokenValidator = TokenValidator(tokenValidationContextHolder, expectedClientIds)

        every { tokenValidationContextHolder.getTokenValidationContext() } returns tokenValidationContext
        every { tokenValidationContext.getClaims("tokenx") } returns jwtTokenClaims
        every { jwtTokenClaims.getStringClaim("client_id") } returns null

        assertThrows<IngenTilgang> {
            tokenValidator.validerTokenXClaims()
        }.message shouldBe "Uventet client id null"
    }

    @Test
    fun `should extract fnr from idporten tokenx claims`() {
        val expectedClientIds = setOf("dev-gcp:team-esyfo:esyfo-proxy")
        val tokenValidator = TokenValidator(tokenValidationContextHolder, expectedClientIds)
        val expectedFnr = "12345678910"

        every { jwtTokenClaims.getStringClaim("pid") } returns expectedFnr

        val fnr = tokenValidator.fnrFraIdportenTokenX(jwtTokenClaims)

        fnr shouldBe expectedFnr
    }
}
