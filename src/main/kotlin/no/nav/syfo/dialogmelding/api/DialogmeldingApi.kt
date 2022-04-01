package no.nav.syfo.dialogmelding.api

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import no.nav.syfo.dialogmelding.DialogmeldingService
import no.nav.syfo.dialogmelding.model.DialogmeldingType
import no.nav.syfo.dialogmelding.model.OpprettDialogmeldingRequest
import no.nav.syfo.log
import no.nav.syfo.model.PersonIdent
import java.util.*

object OpprettDialogmeldingRequestParameters {
    const val type = "type"
    const val pasientFnr = "pasientFnr"
    const val legeFnr = "legeFnr"
    const val notat = "notat"
    const val refToParent = "refToParent"
    const val refToConversation = "refToConversation"
}

fun Route.registerDialogmeldingApi(dialogmeldingService: DialogmeldingService) {
    post("/dialogmelding/opprett") {
        val formParameters = call.receiveParameters()
        val msgId = UUID.randomUUID().toString()
        val request = OpprettDialogmeldingRequest(
            msgId = msgId,
            type = DialogmeldingType.valueOf(formParameters.getOrFail(OpprettDialogmeldingRequestParameters.type)),
            pasientFnr = PersonIdent(formParameters.getOrFail(OpprettDialogmeldingRequestParameters.pasientFnr)),
            legeFnr = PersonIdent(formParameters.getOrFail(OpprettDialogmeldingRequestParameters.legeFnr)),
            notat = formParameters[OpprettDialogmeldingRequestParameters.notat],
            refToParent = formParameters[OpprettDialogmeldingRequestParameters.refToParent],
            refToConversation = formParameters[OpprettDialogmeldingRequestParameters.refToConversation],
        )

        dialogmeldingService.opprettDialogmelding(request)

        log.info("Dialogmelding opprettet")

        call.respond(HttpStatusCode.OK, "Opprettet dialogmelding med msgId $msgId")
    }
}
