package no.nav.syfo.meroppfolging.kafka

import no.nav.syfo.kafka.KafkaEnvironment
import no.nav.syfo.kafka.commonKafkaAivenProducerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import java.util.*

fun kafkaSenOppfolgingSvarProducerConfig(
    kafkaEnvironment: KafkaEnvironment,
): Properties {
    return Properties().apply {
        putAll(commonKafkaAivenProducerConfig(kafkaEnvironment))
        this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaSenOppfolgingSvarSerializer::class.java.canonicalName
    }
}

fun kafkaSenOppfolgingVarselProducerConfig(
    kafkaEnvironment: KafkaEnvironment,
): Properties {
    return Properties().apply {
        putAll(commonKafkaAivenProducerConfig(kafkaEnvironment))
        this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaSenOppfolgingVarselSerializer::class.java.canonicalName
    }
}
