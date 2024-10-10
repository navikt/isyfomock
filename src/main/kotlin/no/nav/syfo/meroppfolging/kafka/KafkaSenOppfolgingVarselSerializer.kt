package no.nav.syfo.meroppfolging.kafka
import no.nav.syfo.meroppfolging.model.SenOppfolgingVarsel
import no.nav.syfo.util.configuredJacksonMapper
import org.apache.kafka.common.serialization.Serializer

class KafkaSenOppfolgingVarselSerializer : Serializer<SenOppfolgingVarsel> {
    private val mapper = configuredJacksonMapper()
    override fun serialize(topic: String?, data: SenOppfolgingVarsel?): ByteArray = mapper.writeValueAsBytes(data)
}
