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
    delete("/moetebehov/slett/{fnr}") {
        try {
            val fnr = call.parameters["fnr"] ?: throw IllegalArgumentException()
            val aktoerId = aktoerService.getAktoerIdFormFnr(fnr) ?: throw IllegalArgumentException()

            call.respond(HttpStatusCode.OK, moetebehovService.deleteMoetebehov(aktoerId))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.NotFound, "Could not find user")
        }
    }
}
