package no.nav.syfo

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.api.apiModule
import no.nav.syfo.esyfovarsel.kafka.kafkaEsyfovarselHendelseProducerConfig
import no.nav.syfo.esyfovarsel.model.EsyfovarselHendelse
import no.nav.syfo.meroppfolging.kafka.kafkaSenOppfolgingSvarProducerConfig
import no.nav.syfo.meroppfolging.kafka.kafkaSenOppfolgingVarselProducerConfig
import no.nav.syfo.meroppfolging.model.SenOppfolgingSvar
import no.nav.syfo.meroppfolging.model.SenOppfolgingVarsel
import no.nav.syfo.mq.MQSender
import no.nav.syfo.reset.kafka.kafkaTestdataResetHendelseProducerConfig
import org.apache.kafka.clients.producer.KafkaProducer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

val log: Logger = LoggerFactory.getLogger("no.nav.syfo.isyfomock")

fun main() {
    val environment = Environment()
    val serviceUser = ServiceUser()
    val applicationState = ApplicationState()

    val mqSender = MQSender(
        environmentMQ = environment.mq,
        serviceUser = serviceUser,
    )

    val apprecMqSender = MQSender(
        environmentMQ = environment.apprecMQ,
        serviceUser = serviceUser,
    )

    val esyfovarselHendelseProducer = KafkaProducer<String, EsyfovarselHendelse>(
        kafkaEsyfovarselHendelseProducerConfig(
            kafkaEnvironment = environment.kafka,
        ),
    )

    val senOppfolgingSvarProducer = KafkaProducer<String, SenOppfolgingSvar>(
        kafkaSenOppfolgingSvarProducerConfig(
            kafkaEnvironment = environment.kafka,
        ),
    )
    val senOppfolgingVarselProducer = KafkaProducer<String, SenOppfolgingVarsel>(
        kafkaSenOppfolgingVarselProducerConfig(
            kafkaEnvironment = environment.kafka,
        ),
    )

    val testdataResetProducer = KafkaProducer<String, String>(
        kafkaTestdataResetHendelseProducerConfig(
            kafkaEnvironment = environment.kafka,
        ),
    )

    val applicationEnvironment = applicationEnvironment {
        log = log
        config = HoconApplicationConfig(ConfigFactory.load())
    }

    val server = embeddedServer(
        Netty,
        environment = applicationEnvironment,
        configure = {
            connector {
                port = environment.applicationPort
            }
            connectionGroupSize = 8
            workerGroupSize = 8
            callGroupSize = 16
        },
        module = {
            apiModule(
                applicationState = applicationState,
                mqSender = mqSender,
                apprecMQSender = apprecMqSender,
                environment = environment,
                esyfovarselHendelseProducer = esyfovarselHendelseProducer,
                senOppfolgingSvarProducer = senOppfolgingSvarProducer,
                senOppfolgingVarselProducer = senOppfolgingVarselProducer,
                testdataResetKafkaProducer = testdataResetProducer,
            )
            monitor.subscribe(ApplicationStarted) {
                applicationState.ready = true
                log.info("Application is ready, running Java VM ${Runtime.version()}")
            }
        },
    )

    Runtime.getRuntime().addShutdownHook(
        Thread {
            server.stop(10, 10, TimeUnit.SECONDS)
        },
    )

    server.start(wait = true)
}
