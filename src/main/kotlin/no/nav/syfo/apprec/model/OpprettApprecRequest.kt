package no.nav.syfo.apprec.model

data class OpprettApprecRequest(
    val status: ApprecStatus,
    val msgId: String,
    val error: String?,
    val errorText: String?,
)
