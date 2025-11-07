package no.nav.syfo.meroppfolging.model

import kotlinx.serialization.Serializable
import no.nav.syfo.util.LocalDateTimeSerializer
import no.nav.syfo.util.UUIDSerializer
import java.time.LocalDateTime
import java.util.*

@Serializable
data class SenOppfolgingSvar(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val personIdent: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    val response: List<SenOppfolgingQuestionV2>,
    @Serializable(with = UUIDSerializer::class)
    val varselId: UUID,
)

@Serializable
data class SenOppfolgingQuestionV2(
    val questionType: SenOppfolgingQuestionTypeV2,
    val questionText: String,
    val answerType: String,
    val answerText: String,
)

enum class SenOppfolgingQuestionTypeV2 {
    FREMTIDIG_SITUASJON,
    BEHOV_FOR_OPPFOLGING,
}
