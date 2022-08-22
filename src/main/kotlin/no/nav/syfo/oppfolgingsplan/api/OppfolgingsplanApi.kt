package no.nav.syfo.oppfolgingsplan

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerOppfolgingsplanApi(
    oppfolgingsplanService: OppfolgingsplanService,
) {
    delete("/oppfolgingsplan/slett/arbeidstaker/{fnr}") {
        try {
            val fnr = call.parameters["fnr"] ?: throw IllegalArgumentException()

            call.respond(HttpStatusCode.OK, oppfolgingsplanService.deleteOppfolgingsplanerForArbeidstaker(fnr))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.NotFound, "Could not find arbeidstaker")
        }
    }

    delete("/oppfolgingsplan/slett/{id}") {
        try {
            val id = call.parameters["id"] ?: throw IllegalArgumentException()

            call.respond(HttpStatusCode.OK, oppfolgingsplanService.deleteOppfolgingsplan(id))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.NotFound, "Could not find oppfolgingsplan with id")
        }
    }
}
