package testhelper

import io.ktor.server.application.*
import io.mockk.mockk
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.api.apiModule
import no.nav.syfo.esyfovarsel.model.EsyfovarselHendelse
import no.nav.syfo.meroppfolging.model.SenOppfolgingSvar
import no.nav.syfo.meroppfolging.model.SenOppfolgingVarsel
import no.nav.syfo.mq.MQSender
import org.apache.kafka.clients.producer.KafkaProducer

fun Application.testApiModule(
    applicationState: ApplicationState = ApplicationState(alive = true, ready = true),
    mqSender: MQSender = mockk(),
    apprecMQSender: MQSender = mockk(),
    esyfovarselHendelseProducer: KafkaProducer<String, EsyfovarselHendelse> = mockk(),
    senOppfolgingSvarProducer: KafkaProducer<String, SenOppfolgingSvar> = mockk(),
    senOppfolgingVarselProducer: KafkaProducer<String, SenOppfolgingVarsel> = mockk(),
    testdataResetProducer: KafkaProducer<String, String> = mockk(),
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
