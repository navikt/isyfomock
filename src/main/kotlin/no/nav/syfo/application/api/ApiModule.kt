package no.nav.syfo.application.api

import io.ktor.server.application.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import no.nav.syfo.aktoer.AktoerService
import no.nav.syfo.aktoer.registerAktoerApi
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.metric.registerMetricApi
import no.nav.syfo.configureJacksonMapper
import no.nav.syfo.dialogmelding.DialogmeldingService
import no.nav.syfo.dialogmelding.api.registerDialogmeldingApi
import no.nav.syfo.mq.MQSender

fun Application.apiModule(
    applicationState: ApplicationState,
    mqSender: MQSender,
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
                    cause.message ?: "Unknown error"
                )
                is IllegalArgumentException -> Pair(
                    HttpStatusCode.BadRequest,
                    cause.message ?: "Unknown error"
                )
                else -> Pair(
                    HttpStatusCode.InternalServerError,
                    "The server reported an unexpected error and cannot complete the request."
                )
            }

            call.respond(response.first, response.second)
        }
    }

    val dialogmeldingService = DialogmeldingService(mqSender = mqSender)
    val aktoerService = AktoerService()

    routing {
        registerPodApi(applicationState = applicationState)
        registerMetricApi()
        registerSwaggerDocApi()
        registerDialogmeldingApi(dialogmeldingService = dialogmeldingService)
        registerAktoerApi(aktoerService = aktoerService)
    }
}
