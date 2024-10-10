package no.nav.syfo.meroppfolging.model

import java.time.LocalDateTime
import java.util.*

data class SenOppfolgingSvar(
    val id: UUID,
    val personIdent: String,
    val createdAt: LocalDateTime,
    val response: List<SenOppfolgingQuestionV2>,
    val varselId: UUID,
)

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
