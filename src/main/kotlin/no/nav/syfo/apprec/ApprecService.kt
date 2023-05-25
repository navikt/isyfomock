package no.nav.syfo.apprec

import no.nav.helse.apprecV1.ObjectFactory
import no.nav.helse.apprecV1.XMLAppRec
import no.nav.syfo.apprec.model.OpprettApprecRequest
import no.nav.syfo.mq.MQSender
import no.nav.syfo.util.fellesformatUnmarshaller
import no.nav.syfo.util.marshallFellesformat
import no.nav.xml.eiff._2.XMLEIFellesformat
import no.nav.xml.eiff._2.XMLMottakenhetBlokk
import java.io.StringReader
import java.util.*
import javax.xml.datatype.DatatypeFactory

private val apprecFilePath = "/fellesformat/apprec.xml"

inline fun <reified T> XMLEIFellesformat.get() = this.any.find { it is T } as T

class ApprecService(private val mqSender: MQSender) {

    fun opprettApprec(request: OpprettApprecRequest) {
        val apprecFellesFormat = tilApprecFellesFormat(request)
        val apprecXml = marshallFellesformat(apprecFellesFormat)
        mqSender.send(apprecXml)
    }

    private fun tilApprecFellesFormat(request: OpprettApprecRequest): XMLEIFellesformat {
        val apprecXml = this::class.java.getResource(apprecFilePath).readText(Charsets.UTF_8)
        val xmlFellesformat = fellesformatUnmarshaller.unmarshal(StringReader(apprecXml)) as XMLEIFellesformat

        xmlFellesformat.get<XMLMottakenhetBlokk>().ediLoggId = UUID.randomUUID().toString()
        xmlFellesformat.get<XMLMottakenhetBlokk>().mottattDatotid =
            DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar())

        xmlFellesformat.get<XMLAppRec>().id = UUID.randomUUID().toString()
        xmlFellesformat.get<XMLAppRec>().status.v = request.status.v
        xmlFellesformat.get<XMLAppRec>().status.dn = request.status.dn
        xmlFellesformat.get<XMLAppRec>().originalMsgId.id = UUID.fromString(request.msgId).toString()
        if (request.error != null) {
            val xmlError = ObjectFactory().createXMLCV()
            xmlError.v = request.error
            xmlError.dn = request.errorText
            xmlFellesformat.get<XMLAppRec>().error.add(xmlError)
        }
        return xmlFellesformat
    }
}
