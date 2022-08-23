package no.nav.syfo.motebehov.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.syfo.motebehov.MotebehovService

fun Route.registerMotebehovApi(
    motebehovService: MotebehovService,
) {
    delete("/motebehov/slett/{fnr}") {
        try {
            val fnr = call.parameters["fnr"] ?: throw IllegalArgumentException()

            call.respond(HttpStatusCode.OK, motebehovService.deleteMotebehov(fnr))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.NotFound, "Could not find user")
        }
    }
}
