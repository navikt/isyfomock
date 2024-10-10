package no.nav.syfo.meroppfolging

import no.nav.syfo.meroppfolging.model.SenOppfolgingVarsel
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import java.util.*

class SenOppfolgingVarselProducer(val kafkaProducer: KafkaProducer<String, SenOppfolgingVarsel>) {
    fun sendVarsel(senOppfolgingVarsel: SenOppfolgingVarsel) {
        try {
            log.info("SenOppfolgingVarselProducer: Sender varsel $senOppfolgingVarsel")
            kafkaProducer.send(
                ProducerRecord(
                    SEN_OPPFOLGING_VARSEL_TOPIC,
                    UUID.randomUUID().toString(),
                    senOppfolgingVarsel,
                ),
            ).get()
        } catch (e: Exception) {
            log.error("Exception was thrown when attempting to send varsel to $SEN_OPPFOLGING_VARSEL_TOPIC. ${e.message}")
            throw e
        }
    }

    companion object {
        const val SEN_OPPFOLGING_VARSEL_TOPIC = "team-esyfo.sen-oppfolging-varsel"
        private val log = LoggerFactory.getLogger(SenOppfolgingVarselProducer::class.java)
    }
}
