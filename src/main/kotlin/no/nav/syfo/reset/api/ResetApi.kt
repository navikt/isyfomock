package no.nav.syfo.esyfovarsel.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.syfo.reset.TestdataResetProducer

fun Route.registerTestdataResetApi(
    producer: TestdataResetProducer,
) {
    post("/reset/{fnr}") {
        val fnr = call.parameters["fnr"] ?: throw IllegalArgumentException()
        call.respond(HttpStatusCode.OK, producer.resetTestdata(fnr))
    }
}
