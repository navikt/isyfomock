package no.nav.syfo.meroppfolging.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import no.nav.syfo.meroppfolging.SenOppfolgingSvarProducer
import no.nav.syfo.meroppfolging.model.SenOppfolgingQuestionV2
import no.nav.syfo.meroppfolging.model.SenOppfolgingSvar
import no.nav.syfo.util.configuredJacksonMapper
import java.time.LocalDateTime
import java.util.*

object SenOppfolgingSvarRequestParameters {
    const val personIdent = "personIdent"
    const val response = "response"
}

fun Route.registerMerOppfolgingApi(
    producer: SenOppfolgingSvarProducer,
) {
    post("/senoppfolging/svar") {
        val mapper = configuredJacksonMapper()
        val formParameters = call.receiveParameters()
        val response = formParameters[SenOppfolgingSvarRequestParameters.response]
        val hendelse = SenOppfolgingSvar(
            id = UUID.randomUUID(),
            personIdent = formParameters.getOrFail(SenOppfolgingSvarRequestParameters.personIdent),
            createdAt = LocalDateTime.now(),
            response = if (response != null) mapper.readValue(response, Array<SenOppfolgingQuestionV2>::class.java).asList() else emptyList(),
        )

        call.respond(HttpStatusCode.OK, producer.sendSvar(hendelse))
    }
}
