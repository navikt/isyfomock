package testhelper

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.mockk
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.api.apiModule
import no.nav.syfo.esyfovarsel.model.EsyfovarselHendelse
import no.nav.syfo.meroppfolging.model.SenOppfolgingSvar
import no.nav.syfo.meroppfolging.model.SenOppfolgingVarsel
import no.nav.syfo.mq.MQSender
import no.nav.syfo.util.configure
import org.apache.kafka.clients.producer.KafkaProducer

fun Application.testApiModule(
    applicationState: ApplicationState = ApplicationState(alive = true, ready = true),
    mqSender: MQSender = mockk(relaxed = true),
    apprecMQSender: MQSender = mockk(relaxed = true),
    esyfovarselHendelseProducer: KafkaProducer<String, EsyfovarselHendelse> = mockk(relaxed = true),
    senOppfolgingSvarProducer: KafkaProducer<String, SenOppfolgingSvar> = mockk(relaxed = true),
    senOppfolgingVarselProducer: KafkaProducer<String, SenOppfolgingVarsel> = mockk(relaxed = true),
    testdataResetProducer: KafkaProducer<String, String> = mockk(relaxed = true),
) {
    this.apiModule(
        applicationState = applicationState,
        mqSender = mqSender,
        apprecMQSender = apprecMQSender,
        environment = testEnvironment(),
        esyfovarselHendelseProducer = esyfovarselHendelseProducer,
        senOppfolgingSvarProducer = senOppfolgingSvarProducer,
        senOppfolgingVarselProducer = senOppfolgingVarselProducer,
        testdataResetKafkaProducer = testdataResetProducer,
    )
}

fun ApplicationTestBuilder.setupApiAndClient(
    apprecMQSender: MQSender = mockk(),
    mqSender: MQSender = mockk(),
    esyfovarselHendelseProducer: KafkaProducer<String, EsyfovarselHendelse> = mockk(),
    senOppfolgingSvarProducer: KafkaProducer<String, SenOppfolgingSvar> = mockk(),
    senOppfolgingVarselProducer: KafkaProducer<String, SenOppfolgingVarsel> = mockk(),
    testdataResetProducer: KafkaProducer<String, String> = mockk(),
): HttpClient {
    application {
        routing {
            application.testApiModule(
                apprecMQSender = apprecMQSender,
                mqSender = mqSender,
                esyfovarselHendelseProducer = esyfovarselHendelseProducer,
                senOppfolgingSvarProducer = senOppfolgingSvarProducer,
                senOppfolgingVarselProducer = senOppfolgingVarselProducer,
                testdataResetProducer = testdataResetProducer,
            )
        }
    }
    return createClient {
        install(ContentNegotiation) {
            jackson { configure() }
        }
    }
}
