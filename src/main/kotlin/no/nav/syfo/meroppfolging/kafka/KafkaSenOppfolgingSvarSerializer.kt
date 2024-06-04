package no.nav.syfo.meroppfolging.kafka
import no.nav.syfo.meroppfolging.model.SenOppfolgingSvar
import no.nav.syfo.util.configuredJacksonMapper
import org.apache.kafka.common.serialization.Serializer

class KafkaSenOppfolgingSvarSerializer : Serializer<SenOppfolgingSvar> {
    private val mapper = configuredJacksonMapper()
    override fun serialize(topic: String?, data: SenOppfolgingSvar?): ByteArray = mapper.writeValueAsBytes(data)
}
