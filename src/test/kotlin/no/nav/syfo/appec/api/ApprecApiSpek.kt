package no.nav.syfo.appec.api

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.*
import no.nav.syfo.apprec.api.OpprettApprecRequestParameters
import no.nav.syfo.apprec.model.ApprecStatus
import no.nav.syfo.mq.MQSender
import org.amshove.kluent.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import testhelper.setupApiAndClient
import java.util.*

class ApprecApiSpek : Spek({

    val defaultRequestParams = arrayOf(
        OpprettApprecRequestParameters.status to ApprecStatus.OK.toString(),
        OpprettApprecRequestParameters.msgId to UUID.randomUUID().toString(),
    )
    val mqSender = mockk<MQSender>()

    describe(ApprecApiSpek::class.java.simpleName) {
        beforeEachTest {
            clearAllMocks()
        }

        describe("Opprett apprec") {
            val url = "/apprec/opprett"
            it("oppretter apprec") {
                testApplication {
                    justRun { mqSender.send(any()) }
                    val requestParameters = listOf(
                        *defaultRequestParams,
                    )
                    val client = setupApiAndClient(mqSender)
                    val response = client.post(url) {
                        header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                        setBody(requestParameters.formUrlEncode())
                    }
                    response.status shouldBeEqualTo HttpStatusCode.OK
                    verify(exactly = 1) { mqSender.send(any()) }
                }
            }
        }
    }
},)
