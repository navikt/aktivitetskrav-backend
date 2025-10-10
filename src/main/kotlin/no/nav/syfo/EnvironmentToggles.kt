package no.nav.syfo

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class EnvironmentToggles(@param:Value("\${NAIS_CLUSTER_NAME}") private val naisCluster: String) {
    fun isProduction() = "prod-gcp" == naisCluster

    fun isDevelopment() = !isProduction()
}
