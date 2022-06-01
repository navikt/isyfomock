package testhelper

import io.ktor.server.application.*
import io.mockk.mockk
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.api.apiModule
import no.nav.syfo.mq.MQSender

fun Application.testApiModule(
    applicationState: ApplicationState = ApplicationState(alive = true, ready = true),
    mqSender: MQSender = mockk()
) {
    this.apiModule(applicationState = applicationState, mqSender = mqSender, environment = testEnvironment())
}
