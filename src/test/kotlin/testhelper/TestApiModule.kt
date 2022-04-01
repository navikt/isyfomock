package testhelper

import io.ktor.application.*
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.api.apiModule

fun Application.testApiModule(applicationState: ApplicationState = ApplicationState(alive = true, ready = true)) {
    this.apiModule(applicationState = applicationState)
}
