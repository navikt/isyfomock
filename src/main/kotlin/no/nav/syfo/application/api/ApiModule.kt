package no.nav.syfo.application.api

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.metric.registerMetricApi
import no.nav.syfo.configureJacksonMapper

fun Application.apiModule(
    applicationState: ApplicationState,
) {
    install(ContentNegotiation) {
        jackson(block = configureJacksonMapper())
    }
    install(StatusPages) {
        exception<Throwable> { cause ->
            log.error("Caught exception", cause)
            call.respond(HttpStatusCode.InternalServerError, cause.message ?: "Unknown error")
        }
    }
    routing {
        registerPodApi(applicationState = applicationState)
        registerMetricApi()
    }
}
