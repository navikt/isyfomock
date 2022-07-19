package no.nav.syfo.moetebehov.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.syfo.aktoer.AktoerService
import no.nav.syfo.moetebehov.MoetebehovService

fun Route.registerMotebehovApi(
    moetebehovService: MoetebehovService,
    aktoerService: AktoerService
) {

    post("/moetebehov/slett/{ssn}") {
        try {
            val ssn = call.parameters["ssn"] ?: throw IllegalArgumentException()
            val aktoerId = aktoerService.getAktoerIdFormSsn(ssn) ?: throw IllegalArgumentException()

            call.respond(HttpStatusCode.OK, moetebehovService.deleteMoetebehov(aktoerId))
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.NotFound, "Could not find user")
        }
    }
}
