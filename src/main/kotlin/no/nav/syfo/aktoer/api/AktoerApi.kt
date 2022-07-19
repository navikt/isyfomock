package no.nav.syfo.aktoer

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerAktoerApi(aktoerService: AktoerService) {
    get("/aktoer/hentAktoerIdByFnr/{fnr}") {
        try {
            val fnr = call.parameters["fnr"] ?: throw IllegalArgumentException()
            val identer = aktoerService.getAktoerIdFormFnr(fnr) ?: throw IllegalArgumentException()

            call.respond(identer)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.NotFound, "Could not find user")
        }
    }

    get("/aktoer/hentFnrByAktoerId/{aktoerId}") {
        try {
            val aktoerId = call.parameters["aktoerId"] ?: throw IllegalArgumentException()
            val identer = aktoerService.getFnrFormAktoerId(aktoerId) ?: throw IllegalArgumentException()

            call.respond(identer)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.NotFound, "Could not find user")
        }
    }
}
