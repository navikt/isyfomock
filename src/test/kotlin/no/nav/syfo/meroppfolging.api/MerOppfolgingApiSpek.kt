package no.nav.syfo.meroppfolging.api

import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import junit.framework.TestCase.assertTrue
import kafka.utils.Json
import no.nav.syfo.meroppfolging.model.SenOppfolgingQuestionTypeV2
import no.nav.syfo.meroppfolging.model.SenOppfolgingQuestionV2
import no.nav.syfo.meroppfolging.model.SenOppfolgingSvar
import no.nav.syfo.meroppfolging.model.SenOppfolgingVarsel
import org.amshove.kluent.internal.assertEquals
import org.amshove.kluent.shouldBeEqualTo
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import testhelper.testApiModule
import java.util.UUID

class MerOppfolgingApiSpek : Spek(
    {
        val varselId = UUID.randomUUID().toString()
        val question = SenOppfolgingQuestionV2(
            SenOppfolgingQuestionTypeV2.BEHOV_FOR_OPPFOLGING,
            "Ja",
            "ja,",
            "ja",
        )
        val svarParams = arrayOf(
            SenOppfolgingSvarRequestParameters.personIdent to "321",
            SenOppfolgingSvarRequestParameters.varselId to varselId,
            SenOppfolgingSvarRequestParameters.response to Json.encodeAsString(
                listOf(
                    question,
                ),
            ),
        )
        val varselParams = arrayOf(
            SenOppfolgingVarselRequestParameters.personIdent to "12345678912",
        )

        with(TestApplicationEngine()) {
            start()

            val svarProducer = mockk<KafkaProducer<String, SenOppfolgingSvar>>(relaxed = true)
            val varselProducer = mockk<KafkaProducer<String, SenOppfolgingVarsel>>(relaxed = true)

            application.testApiModule(senOppfolgingSvarProducer = svarProducer, senOppfolgingVarselProducer = varselProducer)

            describe(MerOppfolgingApiSpek::class.java.simpleName) {
                beforeEachTest {
                    clearAllMocks()
                }

                describe("Svar") {
                    val url = "/senoppfolging/svar"
                    it("Sender svar på kafka") {

                        val capturedRecord = slot<ProducerRecord<String, SenOppfolgingSvar>>()

                        val requestParameters = listOf(
                            *svarParams,
                        )

                        with(
                            handleRequest(HttpMethod.Post, url) {
                                addHeader("Content-Type", ContentType.Application.FormUrlEncoded.toString())
                                setBody(requestParameters.formUrlEncode())
                            },
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK

                            verify(exactly = 1) { svarProducer.send(capture(capturedRecord)) }

                            val capturedRecordValue = capturedRecord.captured.value()
                            assertEquals("321", capturedRecordValue.personIdent)
                            assertTrue(capturedRecordValue.response.contains(question))
                            assertEquals(varselId, capturedRecord.captured.value().varselId.toString())
                        }
                    }
                }

                describe("Varsel") {
                    val url = "/senoppfolging/varsel"

                    it("Sender varsel på kafka") {
                        val capturedRecord = slot<ProducerRecord<String, SenOppfolgingVarsel>>()

                        val requestParameters = listOf(
                            *varselParams,
                        )

                        with(
                            handleRequest(HttpMethod.Post, url) {
                                addHeader("Content-Type", ContentType.Application.FormUrlEncoded.toString())
                                setBody(requestParameters.formUrlEncode())
                            },
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK

                            verify(exactly = 1) { varselProducer.send(capture(capturedRecord)) }

                            val capturedRecordValue = capturedRecord.captured.value()
                            assertEquals("12345678912", capturedRecordValue.personident)
                        }
                    }
                }
            }
        }
    },
)
