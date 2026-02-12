package no.nav.syfo.persistence

import no.nav.syfo.api.dto.Aktivitetsplikt
import no.nav.syfo.api.dto.AktivitetspliktStatus
import no.nav.syfo.kafka.consumer.domain.DocumentComponentDTO
import org.springframework.jdbc.core.RowMapper
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule
import tools.jackson.module.kotlin.readValue
import java.sql.Date
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.ZoneOffset

class AktivitetspliktRowMapper : RowMapper<Aktivitetsplikt> {
    private val jsonWriter: ObjectMapper = JsonMapper.builder()
        .addModule(KotlinModule.Builder().build())
        .build()

    override fun mapRow(rs: ResultSet, rowNum: Int): Aktivitetsplikt {
        val internUuid: String = rs.getString("uuid")
        val status: String = rs.getString("status")
        val arsaker: String? = rs.getString("arsaker").takeIf { it != "" }
        val sistVurdert: Timestamp? = rs.getTimestamp("sist_vurdert")
        val createdAt: Timestamp = rs.getTimestamp("created_at")
        val fristDato: Date? = rs.getDate("svarfrist")
        val journalpostId: String? = rs.getString("journalpost_id")
        val document: String? = rs.getString("document")

        return Aktivitetsplikt(
            internUuid = internUuid,
            status = AktivitetspliktStatus.valueOf(status),
            arsaker = arsaker?.split(",") ?: emptyList(),
            sistVurdert = sistVurdert?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime(),
            createdAt = createdAt.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime(),
            fristDato = fristDato?.toLocalDate(),
            journalpostId = journalpostId,
            document = document?.let { jsonWriter.readValue<List<DocumentComponentDTO>>(it) }
        )
    }
}
