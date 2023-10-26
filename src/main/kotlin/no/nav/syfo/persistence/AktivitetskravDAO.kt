package no.nav.syfo.persistence

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.nav.syfo.api.dto.Aktivitetsplikt
import no.nav.syfo.kafka.consumer.domain.DocumentComponentDTO
import no.nav.syfo.kafka.consumer.domain.KAktivitetskravVarsel
import no.nav.syfo.kafka.consumer.domain.KAktivitetskravVurdering
import no.nav.syfo.service.domain.AktivitetskravVurdering
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Date
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

@Transactional
@Repository
class AktivitetskravDAO(
    val namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
    val jdbcTemplate: JdbcTemplate
) {
    fun storeAktivitetkravVurdering(vurdering: KAktivitetskravVurdering): Int {
        val uuid = UUID.randomUUID()
        val lagreSql = """
            INSERT INTO AKTIVITETSKRAV_VURDERING (
                uuid,
                vurdering_uuid,
                person_ident,
                created_at,
                status,
                beskrivelse,
                arsaker,
                stoppunkt_at,
                updated_by,
                sist_vurdert,
                frist,
                siste_vurdering_uuid
            )
            VALUES (
                :uuid,
                :vurdering_uuid,
                :person_ident,
                :created_at,
                :status,
                :beskrivelse,
                :arsaker,
                :stoppunkt_at,
                :updated_by,
                :sist_vurdert,
                :frist,
                :siste_vurdering_uuid
            )
        """.trimIndent()
        val mapLagreSql = MapSqlParameterSource()
            .addValue("uuid", uuid)
            .addValue("vurdering_uuid", vurdering.uuid)
            .addValue("person_ident", vurdering.personIdent)
            .addValue("created_at", vurdering.createdAt.toTimestamp())
            .addValue("status", vurdering.status)
            .addValue("beskrivelse", vurdering.beskrivelse)
            .addValue("arsaker", vurdering.arsaker.toStr())
            .addValue("stoppunkt_at", vurdering.stoppunktAt.toDate())
            .addValue("updated_by", vurdering.updatedBy)
            .addValue("sist_vurdert", vurdering.sistVurdert?.toTimestamp())
            .addValue("frist", vurdering.frist?.toDate())
            .addValue("siste_vurdering_uuid", vurdering.sisteVurderingUuid)
        return namedParameterJdbcTemplate.update(lagreSql, mapLagreSql)
    }

    fun storeAktivitetkravVarsel(varsel: KAktivitetskravVarsel): Int {
        val uuid = UUID.randomUUID()
        val lagreSql = """
            INSERT INTO AKTIVITETSKRAV_VARSEL (
                uuid,
                person_ident,
                aktivitetskrav_uuid,
                varsel_uuid,
                created_at,
                journalpost_id,
                svarfrist,
                document,
                vurdering_uuid
            )
            VALUES (
                :uuid,
                :person_ident,
                :aktivitetskrav_uuid,
                :varsel_uuid,
                :created_at,
                :journalpost_id,
                :svarfrist,
                :document,
                :vurdering_uuid
            )
        """.trimIndent()
        val mapLagreSql = MapSqlParameterSource()
            .addValue("uuid", uuid)
            .addValue("person_ident", varsel.personIdent)
            .addValue("aktivitetskrav_uuid", varsel.aktivitetskravUuid)
            .addValue("varsel_uuid", varsel.varselUuid)
            .addValue("created_at", varsel.createdAt.toTimestamp())
            .addValue("journalpost_id", varsel.journalpostId)
            .addValue("svarfrist", varsel.svarfrist.toDate())
            .addValue("document", varsel.document.documentsToStr())
            .addValue("vurdering_uuid", varsel.vurderingUuid)
        return namedParameterJdbcTemplate.update(lagreSql, mapLagreSql)
    }

    fun getAktivitetsplikt(fnr: String): Aktivitetsplikt? {
        val query = """
            SELECT vurdering.status, vurdering.arsaker, vurdering.sist_vurdert, vurdering.frist, varsel.journalpost_id
            FROM aktivitetskrav_vurdering vurdering
            LEFT JOIN aktivitetskrav_varsel varsel ON vurdering.siste_vurdering_uuid = varsel.vurdering_uuid
            WHERE vurdering.person_ident = :person_ident
            ORDER BY vurdering.created_at desc, vurdering.sist_vurdert desc NULLS LAST
            LIMIT 1;
        """.trimIndent()

        val namedParameters = MapSqlParameterSource()
            .addValue("person_ident", fnr)

        return try {
            namedParameterJdbcTemplate.queryForObject(query, namedParameters, AktivitetspliktRowMapper())
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    companion object {
        val aktivitetskravVurderingRowMapper: RowMapper<AktivitetskravVurdering>
            get() = RowMapper { rs: ResultSet, _: Int ->
                AktivitetskravVurdering(
                    uuid = UUID.fromString(rs.getString("uuid")),
                    vurderingUuid = UUID.fromString(rs.getString("vurdering_uuid")),
                    personIdent = rs.getString("person_ident"),
                    createdAt = rs.getTimestamp("created_at").toOffsetDateTime(),
                    status = rs.getString("status"),
                    beskrivelse = rs.getString("beskrivelse"),
                    arsaker = rs.getString("arsaker"),
                    stoppunktAt = rs.getDate("stoppunkt_at").toLocalDate(),
                    updatedBy = rs.getString("updated_by"),
                    sisteVurderingUuid = UUID.fromString(rs.getString("siste_vurdering_uuid")),
                    sistVurdert = rs.getTimestamp("sist_vurdert")?.toOffsetDateTime(),
                    frist = rs.getDate("frist").toLocalDate()
                )
            }

        fun LocalDate.toDate() = Date.valueOf(this)

        fun OffsetDateTime.toTimestamp() = Timestamp.valueOf(this.toLocalDateTime())

        private fun Timestamp.toOffsetDateTime() = this.toInstant().atOffset(ZoneOffset.UTC)

        val jsonWriter = ObjectMapper().apply {
            registerKotlinModule()
            registerModule(JavaTimeModule())
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        }
        fun List<String>.toStr() = this.joinToString(separator = ",").trim()

        fun List<DocumentComponentDTO>.documentsToStr() = jsonWriter.writeValueAsString(this)
    }
}
