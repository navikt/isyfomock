package no.nav.syfo.application.api

import io.ktor.server.application.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import no.nav.syfo.Environment
import no.nav.syfo.aktor.AktorService
import no.nav.syfo.aktor.registerAktorApi
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.metric.registerMetricApi
import no.nav.syfo.client.azuread.AzureAdV2Client
import no.nav.syfo.client.pdl.PdlClient
import no.nav.syfo.configureJacksonMapper
import no.nav.syfo.dialogmelding.DialogmeldingService
import no.nav.syfo.dialogmelding.api.registerDialogmeldingApi
import no.nav.syfo.motebehov.MotebehovService
import no.nav.syfo.motebehov.api.registerMotebehovApi
import no.nav.syfo.mq.MQSender
import no.nav.syfo.oppfolgingsplan.OppfolgingsplanService
import no.nav.syfo.oppfolgingsplan.registerOppfolgingsplanApi

fun Application.apiModule(
    applicationState: ApplicationState,
    mqSender: MQSender,
    environment: Environment,
) {
    install(ContentNegotiation) {
        jackson(block = configureJacksonMapper())
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.log.error("Caught exception", cause)

            val response: Pair<HttpStatusCode, String> = when (cause) {
                is ResponseException -> Pair(
                    cause.response.status,
                    cause.message ?: "Unknown error",
                )
                is IllegalArgumentException -> Pair(
                    HttpStatusCode.BadRequest,
                    cause.message ?: "Unknown error",
                )
                else -> Pair(
                    HttpStatusCode.InternalServerError,
                    "The server reported an unexpected error and cannot complete the request.",
                )
            }

            call.respond(response.first, response.second)
        }
    }

    val azureAdV2Client = AzureAdV2Client(
        aadAppClient = environment.aadAppClient,
        aadAppSecret = environment.aadAppSecret,
        aadTokenEndpoint = environment.aadTokenEndpoint,
    )

    val pdlClient = PdlClient(
        azureAdV2Client = azureAdV2Client,
        pdlClientId = environment.pdlClientId,
        pdlUrl = environment.pdlUrl,
    )

    val dialogmeldingService = DialogmeldingService(mqSender = mqSender)
    val motebehovService = MotebehovService(motebehovUrl = environment.motebehovUrl, pdlClient = pdlClient)
    val aktorService = AktorService(pdlClient = pdlClient)
    val oppfolgingsplanService = OppfolgingsplanService(oppfolgingsplanUrl = environment.oppfolgingsplanUrl)

    routing {
        registerPodApi(applicationState = applicationState)
        registerMetricApi()
        registerSwaggerDocApi()
        registerDialogmeldingApi(dialogmeldingService = dialogmeldingService)
        registerMotebehovApi(motebehovService = motebehovService)
        registerAktorApi(aktorService = aktorService)
        registerOppfolgingsplanApi(oppfolgingsplanService = oppfolgingsplanService)
    }
}
