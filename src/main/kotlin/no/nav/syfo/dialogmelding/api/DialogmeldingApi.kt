package no.nav.syfo.dialogmelding.api

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
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
    const val partnerId = "partnerId"
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
            partnerId = formParameters.getOrFail(OpprettDialogmeldingRequestParameters.partnerId),
        )

        dialogmeldingService.opprettDialogmelding(request)

        val message = "Dialogmelding med msgId $msgId opprettet og sendt til padm2"
        log.info(message)
        call.respond(HttpStatusCode.OK, message)
    }
}
