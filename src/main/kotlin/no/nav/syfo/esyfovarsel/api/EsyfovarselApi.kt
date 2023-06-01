package no.nav.syfo.esyfovarsel.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import no.nav.syfo.esyfovarsel.model.ArbeidstakerHendelse
import no.nav.syfo.esyfovarsel.model.HendelseType
import no.nav.syfo.esyfovarsel.model.NarmesteLederHendelse
import no.nav.syfo.esyfovarsel.EsyfovarselProducer

object EsyfovarselNarmesteLederRequestParameters {
    const val type = "type"
    const val ferdigstill = "ferdigstill"
    const val data = "data"
    const val narmesteLederFnr = "narmesteLederFnr"
    const val arbeidstakerFnr = "arbeidstakerFnr"
    const val orgnummer = "orgnummer"
}

object EsyfovarselArbeidstakerRequestParameters {
    const val type = "type"
    const val ferdigstill = "ferdigstill"
    const val data = "data"
    const val arbeidstakerFnr = "arbeidstakerFnr"
    const val orgnummer = "orgnummer"
}

fun Route.registerEsyfovarselApi(
    esyfovarselProducer: EsyfovarselProducer,
) {
    post("/esyfovarsel/arbeidsgiver/send") {
        val formParameters = call.receiveParameters()
        val esyfovarselHendelse = NarmesteLederHendelse(
            type = HendelseType.valueOf(formParameters.getOrFail(EsyfovarselNarmesteLederRequestParameters.type)),
            ferdigstill = formParameters[EsyfovarselNarmesteLederRequestParameters.ferdigstill].toBoolean(),
            data = EsyfovarselNarmesteLederRequestParameters.data,
            narmesteLederFnr = EsyfovarselNarmesteLederRequestParameters.narmesteLederFnr,
            arbeidstakerFnr = EsyfovarselNarmesteLederRequestParameters.arbeidstakerFnr,
            orgnummer = EsyfovarselNarmesteLederRequestParameters.orgnummer,
        )

        call.respond(HttpStatusCode.OK, esyfovarselProducer.sendVarselTilEsyfovarsel(esyfovarselHendelse))
    }

    post("/esyfovarsel/arbeidstaker/send") {
        val formParameters = call.receiveParameters()
        val esyfovarselHendelse = ArbeidstakerHendelse(
            type = HendelseType.valueOf(formParameters.getOrFail(EsyfovarselArbeidstakerRequestParameters.type)),
            ferdigstill = formParameters[EsyfovarselArbeidstakerRequestParameters.ferdigstill].toBoolean(),
            data = EsyfovarselArbeidstakerRequestParameters.data,
            arbeidstakerFnr = EsyfovarselArbeidstakerRequestParameters.arbeidstakerFnr,
            orgnummer = EsyfovarselArbeidstakerRequestParameters.orgnummer,
        )

        call.respond(HttpStatusCode.OK, esyfovarselProducer.sendVarselTilEsyfovarsel(esyfovarselHendelse))
    }
}
