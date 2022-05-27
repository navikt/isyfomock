package no.nav.syfo.dialogmelding.model

import no.nav.syfo.model.PersonIdent

data class OpprettDialogmeldingRequest(
    val type: DialogmeldingType,
    val msgId: String,
    val pasientFnr: PersonIdent,
    val legeFnr: PersonIdent,
    val hprId: String?,
    val legeHerId: String?,
    val notat: String?,
    val refToParent: String?,
    val refToConversation: String?,
    val partnerId: String?,
    val kontorHerId: String?,
)
