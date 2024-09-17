package no.nav.syfo.reset

import no.nav.syfo.esyfovarsel.EsyfovarselProducer
import no.nav.syfo.model.PersonIdent
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import java.util.*

class TestdataResetProducer(
    private val kafkaProducer: KafkaProducer<String, String>,
) {
    fun resetTestdata(
        ident: PersonIdent,
    ) {
        try {
            log.info("TestdataResetProducer:Resetter testdata for $ident")
            kafkaProducer.send(
                ProducerRecord(
                    TESTDATA_RESET_TOPIC,
                    UUID.randomUUID().toString(),
                    ident.value,
                ),
            ).also { it.get() }
        } catch (e: Exception) {
            log.error("Exception was thrown when attempting to send reset message. ${e.message}")
            throw e
        }
    }

    companion object {
        const val TESTDATA_RESET_TOPIC = "teamsykefravr.testdata-reset"
        private val log = LoggerFactory.getLogger(EsyfovarselProducer::class.java)
    }
}
