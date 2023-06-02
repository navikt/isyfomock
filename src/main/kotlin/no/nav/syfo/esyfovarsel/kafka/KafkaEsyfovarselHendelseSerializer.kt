package no.nav.syfo.esyfovarsel.kafka
import no.nav.syfo.esyfovarsel.model.EsyfovarselHendelse
import no.nav.syfo.util.configuredJacksonMapper
import org.apache.kafka.common.serialization.Serializer

class KafkaEsyfovarselHendelseSerializer : Serializer<EsyfovarselHendelse> {
    private val mapper = configuredJacksonMapper()
    override fun serialize(topic: String?, data: EsyfovarselHendelse?): ByteArray = mapper.writeValueAsBytes(data)
}
