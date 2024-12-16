package no.nav.syfo.application.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.util.configure
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEqualTo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import testhelper.testApiModule

class PodApiSpek : Spek({
    fun ApplicationTestBuilder.setupApiAndClient(
        applicationState: ApplicationState,
    ): HttpClient {
        application {
            routing {
                application.testApiModule(
                    applicationState = applicationState,
                )
            }
        }
        return createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                jackson { configure() }
            }
        }
    }

    describe("Successful liveness and readiness checks") {
        it("Successful liveness and readiness checks") {
            testApplication {
                val client = setupApiAndClient(
                    applicationState = ApplicationState(
                        alive = true,
                        ready = true,
                    ),
                )
                val responseLiveness = client.get(podLivenessPath) {}
                responseLiveness.status?.isSuccess() shouldBeEqualTo true
                responseLiveness.body<String>() shouldNotBeEqualTo null

                val responseReady = client.get(podReadinessPath) {}
                responseReady.status?.isSuccess() shouldBeEqualTo true
                responseReady.body<String>() shouldNotBeEqualTo null
            }
        }
    }

    describe("Unsuccessful liveness and readiness checks") {
        it("Unsuccessful liveness and readiness checks") {
            testApplication {
                val client = setupApiAndClient(
                    applicationState = ApplicationState(
                        alive = false,
                        ready = false,
                    ),
                )
                val responseLiveness = client.get(podLivenessPath) {}
                responseLiveness.status shouldBeEqualTo HttpStatusCode.InternalServerError
                responseLiveness.body<String>() shouldNotBeEqualTo null

                val responseReady = client.get(podReadinessPath) {}
                responseReady.status shouldBeEqualTo HttpStatusCode.InternalServerError
                responseReady.body<String>() shouldNotBeEqualTo null
            }
        }
    }
},)
