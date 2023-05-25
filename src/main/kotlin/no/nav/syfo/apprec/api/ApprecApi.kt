package no.nav.syfo.apprec.api

import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import no.nav.syfo.apprec.ApprecService
import no.nav.syfo.apprec.model.ApprecStatus
import no.nav.syfo.apprec.model.OpprettApprecRequest
import no.nav.syfo.log

object OpprettApprecRequestParameters {
    const val status = "status"
    const val errorCode = "errorCode"
    const val errorText = "errorText"
    const val msgId = "msgId"
}

fun Route.registerApprecApi(apprecService: ApprecService) {
    post("/apprec/opprett") {
        val formParameters = call.receiveParameters()
        val request = OpprettApprecRequest(
            status = ApprecStatus.valueOf(formParameters.getOrFail(OpprettApprecRequestParameters.status)),
            error = formParameters[OpprettApprecRequestParameters.errorCode],
            errorText = formParameters[OpprettApprecRequestParameters.errorText],
            msgId = formParameters.getOrFail(OpprettApprecRequestParameters.msgId),
        )

        apprecService.opprettApprec(request)

        val message = "Apprec med status ${request.status} og msgId ${request.msgId} opprettet og sendt"
        log.info(message)
        call.respond(HttpStatusCode.OK, message)
    }
}
