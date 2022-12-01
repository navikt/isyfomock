package no.nav.syfo.dialogmelding

import no.kith.xmlstds.dialog._2006_10_11.XMLDialogmelding
import no.kith.xmlstds.msghead._2006_05_24.XMLConversationRef
import no.kith.xmlstds.msghead._2006_05_24.XMLMsgHead
import no.nav.syfo.dialogmelding.model.DialogmeldingType
import no.nav.syfo.dialogmelding.model.OpprettDialogmeldingRequest
import no.nav.syfo.mq.MQSender
import no.nav.syfo.util.fellesformatUnmarshaller
import no.nav.syfo.util.marshallFellesformat
import no.nav.xml.eiff._2.XMLEIFellesformat
import no.nav.xml.eiff._2.XMLMottakenhetBlokk
import java.io.StringReader
import java.time.LocalDateTime
import java.util.*
import javax.xml.datatype.DatatypeFactory

private val dialogmeldingFilePaths = mapOf(
    DialogmeldingType.VANLIG to "/fellesformat/dialogmelding_dialog_notat.xml",
    DialogmeldingType.VEDLEGG to "/fellesformat/dialogmelding_dialog_notat_med_vedlegg.xml",
    DialogmeldingType.SVAR_MOTEINNKALLING to "/fellesformat/dialogmelding_dialog_svar_innkalling_dialogmote.xml",
    DialogmeldingType.SVAR_FORESPORSEL to "/fellesformat/dialogmelding_dialog_svar_foresporsel_om_pasient.xml",
    DialogmeldingType.SVAR_FORESPORSEL_VEDLEGG to "/fellesformat/dialogmelding_dialog_svar_foresporsel_om_pasient_med_vedlegg.xml",
)

const val MSG_ID_PREFIX = "syfomock"
inline fun <reified T> XMLEIFellesformat.get() = this.any.find { it is T } as T

class DialogmeldingService(private val mqSender: MQSender) {

    fun opprettDialogmelding(request: OpprettDialogmeldingRequest) {
        val dialogmeldingFellesFormat = tilDialogmeldingFellesFormat(request)
        val dialogmeldingXml = marshallFellesformat(dialogmeldingFellesFormat)

        mqSender.send(dialogmeldingXml)
    }

    private fun tilDialogmeldingFellesFormat(request: OpprettDialogmeldingRequest): XMLEIFellesformat {
        val dialogmeldingXml =
            this::class.java.getResource(dialogmeldingFilePaths[request.type]).readText(Charsets.UTF_8)
        val xmlFellesformat = fellesformatUnmarshaller.unmarshal(StringReader(dialogmeldingXml)) as XMLEIFellesformat

        val xmlDialogmelding = xmlFellesformat.get<XMLMsgHead>().document[0].refDoc.content.any[0] as XMLDialogmelding
        xmlFellesformat.get<XMLMsgHead>().document[0].refDoc.content.any[0] = xmlDialogmelding

        xmlFellesformat.get<XMLMsgHead>().msgInfo.msgId = "$MSG_ID_PREFIX-${request.msgId}"
        xmlFellesformat.get<XMLMsgHead>().msgInfo.genDate = LocalDateTime.now()
        xmlFellesformat.get<XMLMottakenhetBlokk>().ediLoggId = UUID.randomUUID().toString()
        xmlFellesformat.get<XMLMottakenhetBlokk>().mottattDatotid =
            DatatypeFactory.newInstance().newXMLGregorianCalendar(GregorianCalendar())
        xmlFellesformat.get<XMLMottakenhetBlokk>().avsenderFnrFraDigSignatur = request.legeFnr.value
        xmlFellesformat.get<XMLMsgHead>().msgInfo.patient.ident.find { it.typeId.v == "FNR" }?.id =
            request.pasientFnr.value

        request.notat?.let { xmlDialogmelding.notat[0].tekstNotatInnhold = it }

        val conversationRef = xmlFellesformat.get<XMLMsgHead>().msgInfo.conversationRef
        request.refToConversation?.let {
            validateConversationRef(conversationRef)
            conversationRef.refToConversation = it
        }
        request.refToParent?.let {
            validateConversationRef(conversationRef)
            conversationRef.refToParent = it
        }

        request.partnerId?.let { xmlFellesformat.get<XMLMottakenhetBlokk>().partnerReferanse = it }

        return xmlFellesformat
    }

    private fun validateConversationRef(conversationRef: XMLConversationRef?) {
        if (conversationRef == null) {
            throw IllegalArgumentException("Kan ikke sette dialog-referanser for denne type dialogmelding")
        }
    }
}
