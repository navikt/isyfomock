package no.nav.syfo.esyfovarsel.api

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.*
import no.nav.syfo.esyfovarsel.model.EsyfovarselHendelse
import no.nav.syfo.esyfovarsel.model.HendelseType
import org.amshove.kluent.shouldBeEqualTo
import org.apache.kafka.clients.producer.KafkaProducer
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import testhelper.setupApiAndClient

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

    val esyfovarselProducer = mockk<KafkaProducer<String, EsyfovarselHendelse>>(relaxed = true)

    describe(EsyfovarselApiSpek::class.java.simpleName) {
        beforeEachTest {
            clearAllMocks()
            justRun { esyfovarselProducer.send(any(), any()) }
        }

        describe("Nærmeste leder") {
            val url = "/esyfovarsel/arbeidsgiver/send"
            it("Sender varsel til nærmeste leder") {
                testApplication {
                    val requestParameters = listOf(
                        *arbeidsgiverRequestParams,
                    )
                    val client = setupApiAndClient(esyfovarselHendelseProducer = esyfovarselProducer)
                    val response = client.post(url) {
                        header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                        setBody(requestParameters.formUrlEncode())
                    }
                    response.status shouldBeEqualTo HttpStatusCode.OK
                    verify(exactly = 1) { esyfovarselProducer.send(any()) }
                }
            }
        }

        describe("Arbeidstaker") {
            val url = "/esyfovarsel/arbeidstaker/send"
            it("Sender varsel til arbeidstaker") {
                testApplication {
                    val requestParameters = listOf(
                        *arbeidstakerRequestParams,
                    )
                    val client = setupApiAndClient(esyfovarselHendelseProducer = esyfovarselProducer)
                    val response = client.post(url) {
                        header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                        setBody(requestParameters.formUrlEncode())
                    }
                    response.status shouldBeEqualTo HttpStatusCode.OK
                    verify(exactly = 1) { esyfovarselProducer.send(any()) }
                }
            }
        }
    }
},)
