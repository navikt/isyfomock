package no.nav.syfo.dialogmelding.api

import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.*
import no.kith.xmlstds.base64container.XMLBase64Container
import no.kith.xmlstds.dialog._2006_10_11.XMLDialogmelding
import no.kith.xmlstds.msghead._2006_05_24.XMLDocument
import no.kith.xmlstds.msghead._2006_05_24.XMLMsgHead
import no.nav.syfo.dialogmelding.MSG_ID_PREFIX
import no.nav.syfo.dialogmelding.get
import no.nav.syfo.dialogmelding.model.DialogmeldingType
import no.nav.syfo.mq.MQSender
import no.nav.syfo.util.fellesformatUnmarshaller
import no.nav.xml.eiff._2.XMLEIFellesformat
import no.nav.xml.eiff._2.XMLMottakenhetBlokk
import org.amshove.kluent.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import testhelper.testApiModule
import java.io.StringReader
import java.util.*

class DialogmeldingApiSpek : Spek({

    val pasientFnr = "12345678912"
    val legeFnr = "23456789123"
    val notat = "kjempefint notat"
    val orgnr = "889640782"
    val svarInnkallingKodeverk = "2.16.578.1.12.4.1.1.8126"
    val henvendelseKodeverk = "2.16.578.1.12.4.1.1.8128"
    val svarForesporselKodeverk = "2.16.578.1.12.4.1.1.9069"

    with(TestApplicationEngine()) {
        start()

        val mqSender = mockk<MQSender>()

        application.testApiModule(mqSender = mqSender)

        describe(DialogmeldingApiSpek::class.java.simpleName) {
            beforeEachTest {
                clearAllMocks()
            }

            describe("Opprett dialogmelding") {
                val url = "/dialogmelding/opprett"
                it("oppretter vanlig dialogmelding") {
                    justRun { mqSender.send(any()) }
                    val requestParameters = listOf(
                        OpprettDialogmeldingRequestParameters.pasientFnr to pasientFnr,
                        OpprettDialogmeldingRequestParameters.notat to notat,
                        OpprettDialogmeldingRequestParameters.legeFnr to legeFnr,
                        OpprettDialogmeldingRequestParameters.type to DialogmeldingType.VANLIG.toString(),
                    )

                    with(
                        handleRequest(HttpMethod.Post, url) {
                            addHeader("Content-Type", ContentType.Application.FormUrlEncoded.toString())
                            setBody(requestParameters.formUrlEncode())
                        }
                    ) {
                        response.status() shouldBeEqualTo HttpStatusCode.OK

                        verify(exactly = 1) { mqSender.send(any()) }
                    }
                }
                it("oppretter dialogmelding med vedlegg") {
                    justRun { mqSender.send(any()) }
                    val requestParameters = listOf(
                        OpprettDialogmeldingRequestParameters.pasientFnr to pasientFnr,
                        OpprettDialogmeldingRequestParameters.notat to notat,
                        OpprettDialogmeldingRequestParameters.legeFnr to legeFnr,
                        OpprettDialogmeldingRequestParameters.type to DialogmeldingType.VEDLEGG.toString(),
                    )

                    with(
                        handleRequest(HttpMethod.Post, url) {
                            addHeader("Content-Type", ContentType.Application.FormUrlEncoded.toString())
                            setBody(requestParameters.formUrlEncode())
                        }
                    ) {
                        response.status() shouldBeEqualTo HttpStatusCode.OK

                        verify(exactly = 1) { mqSender.send(any()) }
                    }
                }
                it("oppretter dialogmelding notat") {
                    val messageSlot = slot<String>()
                    justRun { mqSender.send(capture(messageSlot)) }
                    val requestParameters = listOf(
                        OpprettDialogmeldingRequestParameters.pasientFnr to pasientFnr,
                        OpprettDialogmeldingRequestParameters.notat to notat,
                        OpprettDialogmeldingRequestParameters.legeFnr to legeFnr,
                        OpprettDialogmeldingRequestParameters.type to DialogmeldingType.VANLIG.toString(),
                    )

                    with(
                        handleRequest(HttpMethod.Post, url) {
                            addHeader("Content-Type", ContentType.Application.FormUrlEncoded.toString())
                            setBody(requestParameters.formUrlEncode())
                        }
                    ) {
                        response.status() shouldBeEqualTo HttpStatusCode.OK

                        verify(exactly = 1) { mqSender.send(any()) }

                        val actualFellesformatXml = getOpprettetDialogmeldingFellesformatXml(messageSlot)
                        val dialogmeldingNotat = actualFellesformatXml.dialogmelding.notat.first()

                        dialogmeldingNotat.temaKodet.s shouldBeEqualTo henvendelseKodeverk
                        dialogmeldingNotat.tekstNotatInnhold shouldBeEqualTo notat
                        actualFellesformatXml.mottaksMeta.ediLoggId shouldNotBeEqualTo null
                        actualFellesformatXml.mottaksMeta.avsenderFnrFraDigSignatur shouldBeEqualTo legeFnr
                        actualFellesformatXml.msgHead.msgInfo.msgId shouldContain MSG_ID_PREFIX
                        actualFellesformatXml.msgHead.msgInfo.patient.ident.first().id shouldBeEqualTo pasientFnr
                        actualFellesformatXml.msgHead.msgInfo.receiver.organisation.ident.first().id shouldBeEqualTo orgnr
                        actualFellesformatXml.msgHead.document.size shouldBeEqualTo 1
                    }
                }
                it("oppretter dialogmelding notat med vedlegg") {
                    val messageSlot = slot<String>()
                    justRun { mqSender.send(capture(messageSlot)) }
                    val requestParameters = listOf(
                        OpprettDialogmeldingRequestParameters.pasientFnr to pasientFnr,
                        OpprettDialogmeldingRequestParameters.notat to notat,
                        OpprettDialogmeldingRequestParameters.legeFnr to legeFnr,
                        OpprettDialogmeldingRequestParameters.type to DialogmeldingType.VEDLEGG.toString(),
                    )

                    with(
                        handleRequest(HttpMethod.Post, url) {
                            addHeader("Content-Type", ContentType.Application.FormUrlEncoded.toString())
                            setBody(requestParameters.formUrlEncode())
                        }
                    ) {
                        response.status() shouldBeEqualTo HttpStatusCode.OK

                        verify(exactly = 1) { mqSender.send(any()) }

                        val actualFellesformatXml = getOpprettetDialogmeldingFellesformatXml(messageSlot)
                        val pngVedlegg = actualFellesformatXml.msgHead.document[1] as XMLDocument
                        val jpegVedlegg = actualFellesformatXml.msgHead.document[2] as XMLDocument
                        val tiffVedlegg = actualFellesformatXml.msgHead.document[3] as XMLDocument
                        val dialogmeldingNotat = actualFellesformatXml.dialogmelding.notat.first()

                        dialogmeldingNotat.temaKodet.s shouldBeEqualTo henvendelseKodeverk
                        dialogmeldingNotat.tekstNotatInnhold shouldBeEqualTo notat
                        actualFellesformatXml.mottaksMeta.ediLoggId shouldNotBeEqualTo null
                        actualFellesformatXml.mottaksMeta.avsenderFnrFraDigSignatur shouldBeEqualTo legeFnr
                        actualFellesformatXml.msgHead.msgInfo.msgId shouldContain MSG_ID_PREFIX
                        actualFellesformatXml.msgHead.msgInfo.patient.ident.first().id shouldBeEqualTo pasientFnr
                        actualFellesformatXml.msgHead.msgInfo.receiver.organisation.ident.first().id shouldBeEqualTo orgnr

                        pngVedlegg.refDoc.mimeType shouldBeEqualToIgnoringCase "image/png"
                        jpegVedlegg.refDoc.mimeType shouldBeEqualToIgnoringCase "image/jpeg"
                        tiffVedlegg.refDoc.mimeType shouldBeEqualToIgnoringCase "image/tiff"
                        pngVedlegg.refDoc.content.any.first() shouldBeInstanceOf XMLBase64Container::class.java
                        jpegVedlegg.refDoc.content.any.first() shouldBeInstanceOf XMLBase64Container::class.java
                        tiffVedlegg.refDoc.content.any.first() shouldBeInstanceOf XMLBase64Container::class.java
                    }
                }
                it("oppretter dialogmelding notat svar møte-innkalling") {
                    val messageSlot = slot<String>()
                    justRun { mqSender.send(capture(messageSlot)) }
                    val requestParameters = listOf(
                        OpprettDialogmeldingRequestParameters.pasientFnr to pasientFnr,
                        OpprettDialogmeldingRequestParameters.notat to notat,
                        OpprettDialogmeldingRequestParameters.legeFnr to legeFnr,
                        OpprettDialogmeldingRequestParameters.type to DialogmeldingType.SVAR_MOTEINNKALLING.toString(),
                    )

                    with(
                        handleRequest(HttpMethod.Post, url) {
                            addHeader("Content-Type", ContentType.Application.FormUrlEncoded.toString())
                            setBody(requestParameters.formUrlEncode())
                        }
                    ) {
                        response.status() shouldBeEqualTo HttpStatusCode.OK

                        verify(exactly = 1) { mqSender.send(any()) }

                        val actualFellesformatXml = getOpprettetDialogmeldingFellesformatXml(messageSlot)
                        val dialogmeldingNotat = actualFellesformatXml.dialogmelding.notat.first()

                        dialogmeldingNotat.temaKodet.s shouldBeEqualTo svarInnkallingKodeverk
                        dialogmeldingNotat.tekstNotatInnhold shouldBeEqualTo notat
                        dialogmeldingNotat.foresporsel shouldNotBeEqualTo null

                        actualFellesformatXml.mottaksMeta.ediLoggId shouldNotBeEqualTo null
                        actualFellesformatXml.mottaksMeta.avsenderFnrFraDigSignatur shouldBeEqualTo legeFnr

                        actualFellesformatXml.msgHead.msgInfo.msgId shouldContain MSG_ID_PREFIX
                        actualFellesformatXml.msgHead.msgInfo.patient.ident.first().id shouldBeEqualTo pasientFnr
                        actualFellesformatXml.msgHead.msgInfo.receiver.organisation.ident.first().id shouldBeEqualTo orgnr
                    }
                }
                it("oppretter dialogmelding svar forespørsel") {
                    val messageSlot = slot<String>()
                    justRun { mqSender.send(capture(messageSlot)) }
                    val requestParameters = listOf(
                        OpprettDialogmeldingRequestParameters.pasientFnr to pasientFnr,
                        OpprettDialogmeldingRequestParameters.notat to notat,
                        OpprettDialogmeldingRequestParameters.legeFnr to legeFnr,
                        OpprettDialogmeldingRequestParameters.type to DialogmeldingType.SVAR_FORESPORSEL.toString(),
                    )

                    with(
                        handleRequest(HttpMethod.Post, url) {
                            addHeader("Content-Type", ContentType.Application.FormUrlEncoded.toString())
                            setBody(requestParameters.formUrlEncode())
                        }
                    ) {
                        response.status() shouldBeEqualTo HttpStatusCode.OK

                        verify(exactly = 1) { mqSender.send(any()) }

                        val actualFellesformatXml = getOpprettetDialogmeldingFellesformatXml(messageSlot)
                        val dialogmeldingNotat = actualFellesformatXml.dialogmelding.notat.first()

                        dialogmeldingNotat.temaKodet.s shouldBeEqualTo svarForesporselKodeverk
                        dialogmeldingNotat.tekstNotatInnhold shouldBeEqualTo notat
                        dialogmeldingNotat.foresporsel shouldNotBeEqualTo null

                        actualFellesformatXml.mottaksMeta.ediLoggId shouldNotBeEqualTo null
                        actualFellesformatXml.mottaksMeta.avsenderFnrFraDigSignatur shouldBeEqualTo legeFnr

                        actualFellesformatXml.msgHead.msgInfo.msgId shouldContain MSG_ID_PREFIX
                        actualFellesformatXml.msgHead.msgInfo.patient.ident.first().id shouldBeEqualTo pasientFnr
                        actualFellesformatXml.msgHead.msgInfo.receiver.organisation.ident.first().id shouldBeEqualTo orgnr

                        actualFellesformatXml.msgHead.document.size shouldBeEqualTo 1
                    }
                }
                it("oppretter dialogmelding svar forespørsel med vedlegg") {
                    val messageSlot = slot<String>()
                    justRun { mqSender.send(capture(messageSlot)) }
                    val requestParameters = listOf(
                        OpprettDialogmeldingRequestParameters.pasientFnr to pasientFnr,
                        OpprettDialogmeldingRequestParameters.notat to notat,
                        OpprettDialogmeldingRequestParameters.legeFnr to legeFnr,
                        OpprettDialogmeldingRequestParameters.type to DialogmeldingType.SVAR_FORESPORSEL_VEDLEGG.toString(),
                    )

                    with(
                        handleRequest(HttpMethod.Post, url) {
                            addHeader("Content-Type", ContentType.Application.FormUrlEncoded.toString())
                            setBody(requestParameters.formUrlEncode())
                        }
                    ) {
                        response.status() shouldBeEqualTo HttpStatusCode.OK

                        verify(exactly = 1) { mqSender.send(any()) }

                        val actualFellesformatXml = getOpprettetDialogmeldingFellesformatXml(messageSlot)
                        val pdfVedlegg = actualFellesformatXml.msgHead.document[1] as XMLDocument
                        val dialogmeldingNotat = actualFellesformatXml.dialogmelding.notat.first()

                        dialogmeldingNotat.temaKodet.s shouldBeEqualTo svarForesporselKodeverk
                        dialogmeldingNotat.tekstNotatInnhold shouldBeEqualTo notat
                        dialogmeldingNotat.foresporsel shouldNotBeEqualTo null

                        actualFellesformatXml.mottaksMeta.ediLoggId shouldNotBeEqualTo null
                        actualFellesformatXml.mottaksMeta.avsenderFnrFraDigSignatur shouldBeEqualTo legeFnr

                        actualFellesformatXml.msgHead.msgInfo.msgId shouldContain MSG_ID_PREFIX
                        actualFellesformatXml.msgHead.msgInfo.patient.ident.first().id shouldBeEqualTo pasientFnr
                        actualFellesformatXml.msgHead.msgInfo.receiver.organisation.ident.first().id shouldBeEqualTo orgnr

                        actualFellesformatXml.msgHead.document.size shouldBeEqualTo 5
                        pdfVedlegg.refDoc.mimeType shouldBeEqualToIgnoringCase "application/pdf"
                        pdfVedlegg.refDoc.content.any.first() shouldBeInstanceOf XMLBase64Container::class.java
                    }
                }
                it("oppretter dialogmelding svar møte-innkalling med dialog-refs") {
                    val messageSlot = slot<String>()
                    justRun { mqSender.send(capture(messageSlot)) }

                    val refToConversation = UUID.randomUUID().toString()
                    val refToParent = UUID.randomUUID().toString()
                    val requestParameters = listOf(
                        OpprettDialogmeldingRequestParameters.pasientFnr to pasientFnr,
                        OpprettDialogmeldingRequestParameters.notat to notat,
                        OpprettDialogmeldingRequestParameters.legeFnr to legeFnr,
                        OpprettDialogmeldingRequestParameters.type to DialogmeldingType.SVAR_MOTEINNKALLING.toString(),
                        OpprettDialogmeldingRequestParameters.refToConversation to refToConversation,
                        OpprettDialogmeldingRequestParameters.refToParent to refToParent,
                    )

                    with(
                        handleRequest(HttpMethod.Post, url) {
                            addHeader("Content-Type", ContentType.Application.FormUrlEncoded.toString())
                            setBody(requestParameters.formUrlEncode())
                        }
                    ) {
                        response.status() shouldBeEqualTo HttpStatusCode.OK

                        verify(exactly = 1) { mqSender.send(any()) }

                        val actualFellesformatXml = getOpprettetDialogmeldingFellesformatXml(messageSlot)
                        actualFellesformatXml.msgHead.msgInfo.conversationRef.refToConversation shouldBeEqualTo refToConversation
                        actualFellesformatXml.msgHead.msgInfo.conversationRef.refToParent shouldBeEqualTo refToParent
                    }
                }
                it("gir feilmelding ved opprettelse av vanlig dialogmelding med dialog-refs") {
                    val messageSlot = slot<String>()
                    justRun { mqSender.send(capture(messageSlot)) }

                    val requestParameters = listOf(
                        OpprettDialogmeldingRequestParameters.pasientFnr to pasientFnr,
                        OpprettDialogmeldingRequestParameters.notat to notat,
                        OpprettDialogmeldingRequestParameters.legeFnr to legeFnr,
                        OpprettDialogmeldingRequestParameters.type to DialogmeldingType.VANLIG.toString(),
                        OpprettDialogmeldingRequestParameters.refToConversation to UUID.randomUUID().toString(),
                        OpprettDialogmeldingRequestParameters.refToParent to UUID.randomUUID().toString(),
                    )

                    with(
                        handleRequest(HttpMethod.Post, url) {
                            addHeader("Content-Type", ContentType.Application.FormUrlEncoded.toString())
                            setBody(requestParameters.formUrlEncode())
                        }
                    ) {
                        response.status() shouldBeEqualTo HttpStatusCode.BadRequest

                        verify(exactly = 0) { mqSender.send(any()) }
                    }
                }
                it("oppretter dialogmelding svar møte-innkalling med tomme verdier i frivillige parametere") {
                    val messageSlot = slot<String>()
                    justRun { mqSender.send(capture(messageSlot)) }

                    val requestParameters = listOf(
                        OpprettDialogmeldingRequestParameters.pasientFnr to pasientFnr,
                        OpprettDialogmeldingRequestParameters.notat to null,
                        OpprettDialogmeldingRequestParameters.legeFnr to legeFnr,
                        OpprettDialogmeldingRequestParameters.type to DialogmeldingType.SVAR_MOTEINNKALLING.toString(),
                        OpprettDialogmeldingRequestParameters.refToConversation to null,
                        OpprettDialogmeldingRequestParameters.refToParent to null,
                    )

                    with(
                        handleRequest(HttpMethod.Post, url) {
                            addHeader("Content-Type", ContentType.Application.FormUrlEncoded.toString())
                            setBody(requestParameters.formUrlEncode())
                        }
                    ) {
                        response.status() shouldBeEqualTo HttpStatusCode.OK
                    }
                }
            }
        }
    }
})

fun getOpprettetDialogmeldingFellesformatXml(messageSlot: CapturingSlot<String>): OpprettDialogmeldingFellesformatXml {
    val actualMqMessage = messageSlot.captured
    val actualFellesformatXml =
        fellesformatUnmarshaller.unmarshal(StringReader(actualMqMessage)) as XMLEIFellesformat
    val msgHead = actualFellesformatXml.get<XMLMsgHead>()

    return OpprettDialogmeldingFellesformatXml(
        msgHead = msgHead,
        dialogmelding = msgHead.document.first().refDoc.content.any.first() as XMLDialogmelding,
        mottaksMeta = actualFellesformatXml.get()
    )
}

data class OpprettDialogmeldingFellesformatXml(
    val msgHead: XMLMsgHead,
    val dialogmelding: XMLDialogmelding,
    val mottaksMeta: XMLMottakenhetBlokk,
)
