package no.nav.syfo.application.api

import io.ktor.application.*
import io.ktor.client.features.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
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
        exception<Throwable> { cause ->
            log.error("Caught exception", cause)

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

    routing {
        registerPodApi(applicationState = applicationState)
        registerMetricApi()
        registerSwaggerDocApi()
        registerDialogmeldingApi(dialogmeldingService = dialogmeldingService)
    }
}
