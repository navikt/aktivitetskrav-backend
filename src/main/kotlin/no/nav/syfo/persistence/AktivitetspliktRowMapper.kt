package no.nav.syfo.persistence

import no.nav.syfo.api.dto.Aktivitetsplikt
import no.nav.syfo.api.dto.AktivitetspliktStatus
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.sql.Timestamp

class AktivitetspliktRowMapper : RowMapper<Aktivitetsplikt> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Aktivitetsplikt {
        val status: String = rs.getString("status")
        val arsaker: String? = rs.getString("arsaker")
        val sistVurdert: Timestamp? = rs.getTimestamp("sist_vurdert")
        val fristDato: Timestamp? = rs.getTimestamp("frist")
        val journalpostId: String? = rs.getString("journalpost_id")

        return Aktivitetsplikt(
            status = AktivitetspliktStatus.valueOf(status),
            arsaker = arsaker?.split(",")?.toList() ?: emptyList(),
            sistVurdert = sistVurdert?.toZonedLocalDateTime(),
            fristDato = fristDato?.toZonedLocalDateTime(),
            journalpostId = journalpostId,
        )
    }
}
