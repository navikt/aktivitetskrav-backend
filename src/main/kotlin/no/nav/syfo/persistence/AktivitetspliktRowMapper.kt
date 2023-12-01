package no.nav.syfo.persistence

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.nav.syfo.api.dto.Aktivitetsplikt
import no.nav.syfo.api.dto.AktivitetspliktStatus
import org.springframework.jdbc.core.RowMapper
import java.sql.Date
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.ZoneOffset

class AktivitetspliktRowMapper : RowMapper<Aktivitetsplikt> {
    val jsonWriter = ObjectMapper().apply {
        registerKotlinModule()
        registerModule(JavaTimeModule())
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }

    override fun mapRow(rs: ResultSet, rowNum: Int): Aktivitetsplikt {
        val status: String = rs.getString("status")
        val arsaker: String? = rs.getString("arsaker").takeIf { it != "" }
        val sistVurdert: Timestamp? = rs.getTimestamp("sist_vurdert")
        val createdAt: Timestamp = rs.getTimestamp("created_at")
        val fristDato: Date? = rs.getDate("svarfrist")
        val journalpostId: String? = rs.getString("journalpost_id")
        val document: String? = rs.getString("document")
        val vurderingUuid: String = rs.getString("siste_vurdering_uuid")

        return Aktivitetsplikt(
            status = AktivitetspliktStatus.valueOf(status),
            arsaker = arsaker?.split(",") ?: emptyList(),
            sistVurdert = sistVurdert?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime(),
            createdAt = createdAt.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime(),
            fristDato = fristDato?.toLocalDate(),
            journalpostId = journalpostId,
            document = document?.let { jsonWriter.readValue(it) },
            vurderingUuid = vurderingUuid,
        )
    }
}
