package no.nav.syfo.meroppfolging.model

import java.time.LocalDateTime
import java.util.*

data class SenOppfolgingVarsel(
    val uuid: UUID,
    val personident: String,
    val createdAt: LocalDateTime,
)
