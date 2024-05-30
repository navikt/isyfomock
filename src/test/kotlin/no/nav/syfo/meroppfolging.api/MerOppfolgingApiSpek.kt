package no.nav.syfo.esyfovarsel.api

import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import kafka.utils.Json
import no.nav.syfo.meroppfolging.api.SenOppfolgingSvarRequestParameters
import no.nav.syfo.meroppfolging.model.SenOppfolgingQuestionTypeV2
import no.nav.syfo.meroppfolging.model.SenOppfolgingQuestionV2
import no.nav.syfo.meroppfolging.model.SenOppfolgingSvar
import org.amshove.kluent.shouldBeEqualTo
import org.apache.kafka.clients.producer.KafkaProducer
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import testhelper.testApiModule

class MerOppfolgingApiSpek : Spek(
    {

        val svarParams = arrayOf(
            SenOppfolgingSvarRequestParameters.personIdent to "321",
            SenOppfolgingSvarRequestParameters.response to Json.encodeAsString(SenOppfolgingQuestionV2(SenOppfolgingQuestionTypeV2.ONSKER_OPPFOLGING, "Ja", "ja,", "ja")),
        )

        with(TestApplicationEngine()) {
            start()

            val producer = mockk<KafkaProducer<String, SenOppfolgingSvar>>(relaxed = true)

            application.testApiModule(senOppfolgingSvarProducer = producer)

            describe(MerOppfolgingApiSpek::class.java.simpleName) {
                beforeEachTest {
                    clearAllMocks()
                }

                describe("Svar") {
                    val url = "/senoppfolging/svar"
                    it("Sender svar på kafka") {

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

                            verify(exactly = 1) { producer.send(any()) }
                        }
                    }
                }
            }
        }
    },
)
