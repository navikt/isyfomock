package testhelper

import no.nav.syfo.Environment
import no.nav.syfo.EnvironmentMQ
import no.nav.syfo.application.*
import java.net.ServerSocket
import java.util.*

fun testEnvironment(
    azureTokenEndpoint: String = "azureTokenEndpoint",
    pdlUrl: String? = null,
) = Environment(
    aadAppClient = "isyfomock-client-id",
    aadAppSecret = "isyfomock-secret",
    aadTokenEndpoint = azureTokenEndpoint,
    azureAppWellKnownUrl = "wellknown",
    mq = EnvironmentMQ(
        mqQueueManager = "mogateway-name",
        mqHostname = "mogateway-hostname",
        mqApplicationName = "isyfomock",
        mqPort = 1234,
        mqChannelName = "mogateway-channel-name",
        padm2Queuename = "padm2-queuename",
    ),
    redisHost = "localhost",
    redisPort = 6599,
    redisSecret = "password",
    pdlClientId = "pdlClientId",
    pdlUrl = pdlUrl ?: "http://pdl",
)

// TODO: unused?
fun testAppState() = ApplicationState(
    alive = true,
    ready = true
)

fun getRandomPort() = ServerSocket(0).use {
    it.localPort
}

fun Properties.overrideForTest(): Properties = apply {
    remove("security.protocol")
    remove("sasl.mechanism")
}
