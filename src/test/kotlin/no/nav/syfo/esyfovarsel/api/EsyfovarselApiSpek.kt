package no.nav.syfo.esyfovarsel.api

import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import no.nav.syfo.esyfovarsel.model.EsyfovarselHendelse
import no.nav.syfo.esyfovarsel.model.HendelseType
import org.amshove.kluent.shouldBeEqualTo
import org.apache.kafka.clients.producer.KafkaProducer
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import testhelper.testApiModule

class EsyfovarselApiSpek : Spek({

    val arbeidsgiverRequestParams = arrayOf(
        EsyfovarselNarmesteLederRequestParameters.type to HendelseType.NL_DIALOGMOTE_SVAR_MOTEBEHOV.toString(),
        EsyfovarselNarmesteLederRequestParameters.narmesteLederFnr to "123",
        EsyfovarselNarmesteLederRequestParameters.arbeidstakerFnr to "321",
        EsyfovarselNarmesteLederRequestParameters.orgnummer to "123456789",
        EsyfovarselNarmesteLederRequestParameters.data to "{\n" +
            "  \"tilbakemelding\": \"Vi har vurdert at det ikke trengs møte akkurat nå\"\n" +
            "}",
    )

    val arbeidstakerRequestParams = arrayOf(
        EsyfovarselNarmesteLederRequestParameters.type to HendelseType.SM_DIALOGMOTE_SVAR_MOTEBEHOV.toString(),
        EsyfovarselNarmesteLederRequestParameters.arbeidstakerFnr to "321",
        EsyfovarselNarmesteLederRequestParameters.orgnummer to "123456789",
    )

    with(TestApplicationEngine()) {
        start()

        val esyfovarselProducer = mockk<KafkaProducer<String, EsyfovarselHendelse>>(relaxed = true)

        application.testApiModule(esyfovarselHendelseProducer = esyfovarselProducer)

        describe(EsyfovarselApiSpek::class.java.simpleName) {
            beforeEachTest {
                clearAllMocks()
            }

            describe("Nærmeste leder") {
                val url = "/esyfovarsel/arbeidsgiver/send"
                it("Sender varsel til nærmeste leder") {

                    val requestParameters = listOf(
                        *arbeidsgiverRequestParams,
                    )

                    with(
                        handleRequest(HttpMethod.Post, url) {
                            addHeader("Content-Type", ContentType.Application.FormUrlEncoded.toString())
                            setBody(requestParameters.formUrlEncode())
                        },
                    ) {
                        response.status() shouldBeEqualTo HttpStatusCode.OK

                        verify(exactly = 1) { esyfovarselProducer.send(any()) }
                    }
                }
            }

            describe("Arbeidstaker") {
                val url = "/esyfovarsel/arbeidstaker/send"
                it("Sender varsel til arbeidstaker") {

                    val requestParameters = listOf(
                        *arbeidstakerRequestParams,
                    )

                    with(
                        handleRequest(HttpMethod.Post, url) {
                            addHeader("Content-Type", ContentType.Application.FormUrlEncoded.toString())
                            setBody(requestParameters.formUrlEncode())
                        },
                    ) {
                        response.status() shouldBeEqualTo HttpStatusCode.OK

                        verify(exactly = 1) { esyfovarselProducer.send(any()) }
                    }
                }
            }
        }
    }
},)
