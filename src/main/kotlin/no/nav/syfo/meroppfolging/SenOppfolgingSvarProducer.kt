package no.nav.syfo.meroppfolging

import no.nav.syfo.meroppfolging.model.SenOppfolgingSvar
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import java.util.*

class SenOppfolgingSvarProducer(
    private val kafkaProducer: KafkaProducer<String, SenOppfolgingSvar>,
) {
    fun sendSvar(
        senOppfolgingSvar: SenOppfolgingSvar,
    ) {
        try {
            log.info("SenOppfolgingSvarProducer: Sender svar $senOppfolgingSvar")
            kafkaProducer.send(
                ProducerRecord(
                    SEN_OPPFOLGING_SVAR_TOPIC,
                    UUID.randomUUID().toString(),
                    senOppfolgingSvar,
                ),
            ).get()
        } catch (e: Exception) {
            log.error("Exception was thrown when attempting to send svar to $SEN_OPPFOLGING_SVAR_TOPIC. ${e.message}")
            throw e
        }
    }

    companion object {
        const val SEN_OPPFOLGING_SVAR_TOPIC = "team-esyfo.sen-oppfolging-svar"
        private val log = LoggerFactory.getLogger(SenOppfolgingSvarProducer::class.java)
    }
}
