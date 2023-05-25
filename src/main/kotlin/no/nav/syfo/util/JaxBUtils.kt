package no.nav.syfo.util

import com.migesok.jaxb.adapter.javatime.LocalDateTimeXmlAdapter
import com.migesok.jaxb.adapter.javatime.LocalDateXmlAdapter
import no.kith.xmlstds.base64container.XMLBase64Container
import no.kith.xmlstds.dialog._2006_10_11.XMLDialogmelding
import no.kith.xmlstds.msghead._2006_05_24.XMLMsgHead
import no.nav.helse.apprecV1.XMLAppRec
import no.nav.xml.eiff._2.XMLEIFellesformat
import no.nav.xml.eiff._2.XMLMottakenhetBlokk
import java.io.StringWriter
import java.time.*
import javax.xml.bind.*
import javax.xml.transform.stream.StreamResult

val fellesformatJaxBContext: JAXBContext = JAXBContext.newInstance(
    XMLEIFellesformat::class.java,
    XMLMsgHead::class.java,
    XMLMottakenhetBlokk::class.java,
    XMLDialogmelding::class.java,
    XMLBase64Container::class.java,
    XMLAppRec::class.java,
)

val fellesformatUnmarshaller: Unmarshaller = fellesformatJaxBContext.createUnmarshaller().apply {
    setAdapter(LocalDateTimeXmlAdapter::class.java, XMLDateTimeAdapter())
    setAdapter(LocalDateXmlAdapter::class.java, XMLDateAdapter())
}

fun marshallFellesformat(element: Any?): String {
    return try {
        val writer = StringWriter()
        val marshaller = fellesformatJaxBContext.createMarshaller()
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8")
        marshaller.marshal(element, StreamResult(writer))
        writer.toString()
    } catch (e: JAXBException) {
        throw RuntimeException(e)
    }
}

class XMLDateTimeAdapter : LocalDateTimeXmlAdapter() {
    override fun unmarshal(stringValue: String?): LocalDateTime? = when (stringValue) {
        null -> null
        else -> DatatypeConverter.parseDateTime(stringValue).toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
    }
}

class XMLDateAdapter : LocalDateXmlAdapter() {
    override fun unmarshal(stringValue: String?): LocalDate? = when (stringValue) {
        null -> null
        else -> DatatypeConverter.parseDate(stringValue).toInstant().atZone(ZoneOffset.UTC).toLocalDate()
    }
}
