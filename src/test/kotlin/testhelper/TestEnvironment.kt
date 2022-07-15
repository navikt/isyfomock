package testhelper

import no.nav.syfo.Environment
import no.nav.syfo.EnvironmentMQ
fun testEnvironment(
    azureTokenEndpoint: String = "azureTokenEndpoint",
    pdlUrl: String? = null,
) = Environment(
    aadAppClient = "isyfomock-client-id",
    aadAppSecret = "isyfomock-secret",
    aadTokenEndpoint = azureTokenEndpoint,
    mq = EnvironmentMQ(
        mqQueueManager = "mogateway-name",
        mqHostname = "mogateway-hostname",
        mqApplicationName = "isyfomock",
        mqPort = 1234,
        mqChannelName = "mogateway-channel-name",
        padm2Queuename = "padm2-queuename",
    ),
    pdlClientId = "pdlClientId",
    pdlUrl = pdlUrl ?: "http://pdl",
)
