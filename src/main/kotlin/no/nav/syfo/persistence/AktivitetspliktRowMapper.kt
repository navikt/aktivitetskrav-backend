package no.nav.syfo.persistence

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.syfo.api.dto.Aktivitetsplikt
import no.nav.syfo.api.dto.AktivitetspliktStatus
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.sql.Timestamp

class AktivitetspliktRowMapper : RowMapper<Aktivitetsplikt> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Aktivitetsplikt {
        val status: String = rs.getString("status")
        val arsaker: String? = rs.getString("arsaker").takeIf { it != "" }
        val sistVurdert: Timestamp? = rs.getTimestamp("sist_vurdert")
        val fristDato: Timestamp? = rs.getTimestamp("svarfrist")
        val journalpostId: String? = rs.getString("journalpost_id")
        val dokument: String? = rs.getString("document")

        return Aktivitetsplikt(
            status = AktivitetspliktStatus.valueOf(status),
            arsaker = arsaker?.split(",") ?: emptyList(),
            sistVurdert = sistVurdert?.toZonedLocalDateTime(),
            fristDato = fristDato?.toZonedLocalDateTime(),
            journalpostId = journalpostId,
            dokument = dokument?.let { jsonWriter.readValue(it) }
        )
    }
}
