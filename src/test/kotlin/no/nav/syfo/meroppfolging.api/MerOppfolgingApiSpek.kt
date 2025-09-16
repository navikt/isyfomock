package no.nav.syfo.meroppfolging.api

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.*
import kafka.utils.Json
import no.nav.syfo.meroppfolging.model.SenOppfolgingQuestionTypeV2
import no.nav.syfo.meroppfolging.model.SenOppfolgingQuestionV2
import no.nav.syfo.meroppfolging.model.SenOppfolgingSvar
import no.nav.syfo.meroppfolging.model.SenOppfolgingVarsel
import org.amshove.kluent.internal.assertEquals
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import java.util.concurrent.Future
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import testhelper.setupApiAndClient
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
            SenOppfolgingSvarRequestParameters.personident to "321",
            SenOppfolgingSvarRequestParameters.varselId to varselId,
            SenOppfolgingSvarRequestParameters.response to Json.encodeAsString(
                listOf(
                    question,
                ),
            ),
        )
        val varselParams = arrayOf(
            SenOppfolgingVarselRequestParameters.personident to "12345678912",
        )
        val svarProducer = mockk<KafkaProducer<String, SenOppfolgingSvar>>(relaxed = true)
        val varselProducer = mockk<KafkaProducer<String, SenOppfolgingVarsel>>(relaxed = true)

        describe(MerOppfolgingApiSpek::class.java.simpleName) {
            beforeEachTest {
                clearAllMocks()
            }

            describe("Svar") {
                val url = "/senoppfolging/svar"
                it("Sender svar på kafka") {
                    testApplication {
                        val capturedRecord = slot<ProducerRecord<String, SenOppfolgingSvar>>()
                        // Mock future and record metadata to satisfy code calling future.get()
                        val mockFuture = mockk<Future<RecordMetadata>>()
                        val mockRecordMetadata = mockk<RecordMetadata>()
                        every { mockFuture.get() } returns mockRecordMetadata
                        every { svarProducer.send(capture(capturedRecord)) } returns mockFuture
                        val requestParameters = listOf(
                            *svarParams,
                        )
                        val client = setupApiAndClient(
                            senOppfolgingSvarProducer = svarProducer,
                            senOppfolgingVarselProducer = varselProducer,
                        )
                        val response = client.post(url) {
                            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                            setBody(requestParameters.formUrlEncode())
                        }
                        response.status shouldBeEqualTo HttpStatusCode.OK
                        verify(exactly = 1) { svarProducer.send(any()) }
                        val capturedRecordValue = capturedRecord.captured.value()
                        assertEquals("321", capturedRecordValue.personIdent)
                        capturedRecordValue.response shouldContain question
                        assertEquals(varselId, capturedRecord.captured.value().varselId.toString())
                    }
                }
            }

            describe("Varsel") {
                val url = "/senoppfolging/varsel"

                it("Sender varsel på kafka") {
                    testApplication {
                        val capturedRecord = slot<ProducerRecord<String, SenOppfolgingVarsel>>()
                        val mockFuture = mockk<Future<RecordMetadata>>()
                        val mockRecordMetadata = mockk<RecordMetadata>()
                        every { mockFuture.get() } returns mockRecordMetadata
                        every { varselProducer.send(capture(capturedRecord)) } returns mockFuture
                        val requestParameters = listOf(
                            *varselParams,
                        )
                        val client = setupApiAndClient(
                            senOppfolgingSvarProducer = svarProducer,
                            senOppfolgingVarselProducer = varselProducer,
                        )
                        val response = client.post(url) {
                            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                            setBody(requestParameters.formUrlEncode())
                        }
                        response.status shouldBeEqualTo HttpStatusCode.OK
                        verify(exactly = 1) { varselProducer.send(any()) }

                        val capturedRecordValue = capturedRecord.captured.value()
                        assertEquals("12345678912", capturedRecordValue.personident)
                    }
                }
            }
        }
    },
)
