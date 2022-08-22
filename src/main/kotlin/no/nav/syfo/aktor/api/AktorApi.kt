package no.nav.syfo.aktor

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerAktorApi(aktorService: AktorService) {
    get("/aktor/hentAktorIdByFnr/{fnr}") {
        try {
            val fnr = call.parameters["fnr"] ?: throw IllegalArgumentException()
            val identer = aktorService.getAktorIdFromFnr(fnr) ?: throw IllegalArgumentException()

            call.respond(identer)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.NotFound, "Could not find user")
        }
    }

    get("/aktor/hentFnrByAktorId/{aktorId}") {
        try {
            val aktorId = call.parameters["aktorId"] ?: throw IllegalArgumentException()
            val identer = aktorService.getFnrFromAktorId(aktorId) ?: throw IllegalArgumentException()

            call.respond(identer)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.NotFound, "Could not find user")
        }
    }
}
