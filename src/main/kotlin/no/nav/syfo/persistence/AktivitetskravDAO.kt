package no.nav.syfo.persistence

import no.nav.syfo.api.dto.Aktivitetsplikt
import no.nav.syfo.kafka.consumer.domain.KAktivitetskravVarsel
import no.nav.syfo.kafka.consumer.domain.KAktivitetskravVurdering
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@Repository
class AktivitetskravDAO(val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) {
    fun storeAktivitetkravVurdering(vurdering: KAktivitetskravVurdering) {
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
        namedParameterJdbcTemplate.update(lagreSql, mapLagreSql)
    }

    fun storeAktivitetkravVarsel(varsel: KAktivitetskravVarsel) {
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
        namedParameterJdbcTemplate.update(lagreSql, mapLagreSql)
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
}
