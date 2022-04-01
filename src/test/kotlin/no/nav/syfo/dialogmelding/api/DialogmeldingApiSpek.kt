package no.nav.syfo.dialogmelding.api

import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.*
import no.nav.syfo.dialogmelding.model.*
import no.nav.syfo.mq.MQSender
import org.amshove.kluent.shouldBeEqualTo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import testhelper.testApiModule

class DialogmeldingApiSpek : Spek({

    val pasientFnr = "12345678912"
    val legeFnr = "23456789123"
    val notat = "kjempefint notat"

    with(TestApplicationEngine()) {
        start()

        val mqSender = mockk<MQSender>()
        justRun { mqSender.send(any()) }

        application.testApiModule(mqSender = mqSender)

        describe(DialogmeldingApiSpek::class.java.simpleName) {
            describe("Opprett dialogmelding") {
                val url = "/dialogmelding/opprett"
                it("oppretter vanlig dialogmelding") {
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
            }
        }
    }
})
