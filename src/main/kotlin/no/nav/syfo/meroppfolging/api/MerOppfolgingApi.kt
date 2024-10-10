package no.nav.syfo.meroppfolging.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import no.nav.syfo.meroppfolging.SenOppfolgingSvarProducer
import no.nav.syfo.meroppfolging.SenOppfolgingVarselProducer
import no.nav.syfo.meroppfolging.model.SenOppfolgingQuestionV2
import no.nav.syfo.meroppfolging.model.SenOppfolgingSvar
import no.nav.syfo.meroppfolging.model.SenOppfolgingVarsel
import no.nav.syfo.util.configuredJacksonMapper
import java.time.LocalDateTime
import java.util.*

object SenOppfolgingSvarRequestParameters {
    const val personident = "personident"
    const val varselId = "varselId"
    const val response = "response"
}

object SenOppfolgingVarselRequestParameters {
    const val personident = "personident"
}

fun Route.registerMerOppfolgingApi(
    svarProducer: SenOppfolgingSvarProducer,
    varselProducer: SenOppfolgingVarselProducer,
) {
    post("/senoppfolging/svar") {
        val mapper = configuredJacksonMapper()
        val formParameters = call.receiveParameters()
        val response = formParameters[SenOppfolgingSvarRequestParameters.response]
        val hendelse = SenOppfolgingSvar(
            id = UUID.randomUUID(),
            varselId = UUID.fromString(formParameters.getOrFail(SenOppfolgingSvarRequestParameters.varselId)),
            personIdent = formParameters.getOrFail(SenOppfolgingSvarRequestParameters.personident),
            createdAt = LocalDateTime.now(),
            response = if (response != null) mapper.readValue(response, Array<SenOppfolgingQuestionV2>::class.java).asList() else emptyList(),
        )

        call.respond(HttpStatusCode.OK, svarProducer.sendSvar(hendelse))
    }

    post("/senoppfolging/varsel") {
        val formParameters = call.receiveParameters()
        val personIdent = formParameters.getOrFail(SenOppfolgingVarselRequestParameters.personident)
        val varselUuid = UUID.randomUUID()
        val senOppfolgingVarsel = SenOppfolgingVarsel(
            uuid = varselUuid,
            personident = personIdent,
            createdAt = LocalDateTime.now(),
        )
        varselProducer.sendVarsel(senOppfolgingVarsel)

        call.respond(HttpStatusCode.OK, "Varsel med uuid $varselUuid sendt til varsel-topic")
    }
}
