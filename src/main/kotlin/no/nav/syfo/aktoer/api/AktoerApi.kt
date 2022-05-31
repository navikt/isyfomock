package no.nav.syfo.aktoer

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerAktoerApi(aktoerService: AktoerService) {
    get("/aktoer/hentAktoerIdBySsn/{ssn}") {
        call.respond(HttpStatusCode.OK)
    }

    get("/aktoer/hentFnrByAktoerId/{aktoerId}") {
        call.respond(HttpStatusCode.OK)
    }
}
