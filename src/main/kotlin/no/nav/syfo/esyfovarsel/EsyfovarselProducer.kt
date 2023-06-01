package no.nav.syfo.esyfovarsel
import no.nav.syfo.esyfovarsel.model.EsyfovarselHendelse
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory

import java.util.*

class EsyfovarselProducer(
    private val kafkaProducer: KafkaProducer<String, EsyfovarselHendelse>,
) {
    fun sendVarselTilEsyfovarsel(
        esyfovarselHendelse: EsyfovarselHendelse,
    ) {
        try {
            log.info("EsyfovarselProducer: Sender varsel av type ${esyfovarselHendelse.type.name}")
            kafkaProducer.send(
                ProducerRecord(
                    ESYFOVARSEL_TOPIC,
                    UUID.randomUUID().toString(),
                    esyfovarselHendelse,
                ),
            ).get()
        } catch (e: Exception) {
            log.error("Exception was thrown when attempting to send varsel to esyfovarsel. ${e.message}")
            throw e
        }
    }

    companion object {
        const val ESYFOVARSEL_TOPIC = "team-esyfo.varselbus"
        private val log = LoggerFactory.getLogger(EsyfovarselProducer::class.java)
    }
}
