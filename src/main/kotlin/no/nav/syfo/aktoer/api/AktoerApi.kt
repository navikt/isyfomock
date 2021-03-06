package no.nav.syfo.aktoer

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerAktoerApi(aktoerService: AktoerService) {
    get("/aktoer/hentAktoerIdBySsn/{ssn}") {
        try {
            val ssn = call.parameters["ssn"] ?: throw IllegalArgumentException()
            val identer = aktoerService.getAktoerIdFormSsn(ssn) ?: throw IllegalArgumentException()

            call.respond(identer)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.NotFound)
        }
    }

    get("/aktoer/hentSsnByAktoerId/{aktoerId}") {
        try {
            val aktoerId = call.parameters["aktoerId"] ?: throw IllegalArgumentException()
            val identer = aktoerService.getSsnFormAktoerId(aktoerId) ?: throw IllegalArgumentException()

            call.respond(identer)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}
