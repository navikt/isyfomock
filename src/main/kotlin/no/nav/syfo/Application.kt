package no.nav.syfo

import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.api.apiModule
import no.nav.syfo.mq.MQSender
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

val log: Logger = LoggerFactory.getLogger("no.nav.syfo.isyfomock")

fun main() {
    val environment = Environment()
    val serviceUser = ServiceUser()
    val mqSender = MQSender(
        environmentMQ = environment.mq,
        serviceUser = serviceUser
    )
    val applicationState = ApplicationState()
    val applicationEngineEnvironment = applicationEngineEnvironment {
        connector {
            port = environment.applicationPort
        }
        module {
            apiModule(
                mqSender = mqSender,
                applicationState = applicationState,
            )
        }
    }

    applicationEngineEnvironment.monitor.subscribe(ApplicationStarted) { application ->
        applicationState.ready = true
        application.environment.log.info("Application is ready")
    }

    val server = embeddedServer(
        factory = Netty,
        environment = applicationEngineEnvironment,
    )

    Runtime.getRuntime().addShutdownHook(
        Thread {
            server.stop(10, 10, TimeUnit.SECONDS)
        }
    )

    server.start(wait = false)
}
