package no.nav.syfo.motebehov.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.syfo.motebehov.MotebehovService

fun Route.registerMotebehovApi(motebehovService: MotebehovService) {
    post("/motebehov/slett") {

        call.respond(HttpStatusCode.OK, "Test: Ok")
    }
}
