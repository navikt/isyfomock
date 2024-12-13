package no.nav.syfo.reset.api

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.*
import org.amshove.kluent.shouldBeEqualTo
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.RecordMetadata
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import testhelper.setupApiAndClient
import java.util.concurrent.Future

class ResetApiSpek : Spek({
    val testdataResetProducer = mockk<KafkaProducer<String, String>>()

    describe(ResetApiSpek::class.java.simpleName) {
        beforeEachTest {
            clearAllMocks()
        }

        describe("Resetter testdata for bruker") {
            it("Resetter testdata med gyldig fnr") {
                testApplication {
                    coEvery {
                        testdataResetProducer.send(any())
                    } returns mockk<Future<RecordMetadata>>(relaxed = true)
                    val url = "/reset/12345678910"
                    val client = setupApiAndClient(testdataResetProducer = testdataResetProducer)
                    val response = client.post(url) {}
                    response.status shouldBeEqualTo HttpStatusCode.OK
                    verify(exactly = 1) { testdataResetProducer.send(any()) }
                }
            }

            it("Resetter testdata med ugyldig fnr") {
                testApplication {
                    val url = "/reset/12345678"
                    val client = setupApiAndClient(testdataResetProducer = testdataResetProducer)
                    val response = client.post(url) {}
                    response.status shouldBeEqualTo HttpStatusCode.BadRequest
                    verify(exactly = 0) { testdataResetProducer.send(any()) }
                }
            }
        }
    }
},)
