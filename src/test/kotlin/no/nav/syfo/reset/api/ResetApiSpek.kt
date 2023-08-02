package no.nav.syfo.reset.api

import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.shouldBeEqualTo
import org.apache.kafka.clients.producer.KafkaProducer
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import testhelper.testApiModule

class ResetApiSpek : Spek(
    {

        with(TestApplicationEngine()) {
            start()

            val testdataResetProducer = mockk<KafkaProducer<String, String>>(relaxed = true)

            application.testApiModule(testdataResetProducer = testdataResetProducer)

            describe(ResetApiSpek::class.java.simpleName) {
                beforeEachTest {
                    clearAllMocks()
                }

                describe("Resetter testdata for bruker") {
                    it("Resetter testdata med gyldig fnr") {
                        val url = "/reset/12345678910"
                        with(
                            handleRequest(HttpMethod.Post, url) {
                                addHeader("Content-Type", ContentType.Application.FormUrlEncoded.toString())
                            },
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.OK

                            verify(exactly = 1) { testdataResetProducer.send(any()) }
                        }
                    }

                    it("Resetter testdata med ugyldig fnr") {
                        val url = "/reset/12345678"
                        with(
                            handleRequest(HttpMethod.Post, url) {
                                addHeader("Content-Type", ContentType.Application.FormUrlEncoded.toString())
                            },
                        ) {
                            response.status() shouldBeEqualTo HttpStatusCode.BadRequest

                            verify(exactly = 0) { testdataResetProducer.send(any()) }
                        }
                    }
                }
            }
        }
    },
)
