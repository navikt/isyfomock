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
        mqQueuename = "padm2-queuename",
    ),
    apprecMQ = EnvironmentMQ(
        mqQueueManager = "apprec-mogateway-name",
        mqHostname = "apprec-mogateway-hostname",
        mqApplicationName = "isyfomock",
        mqPort = 1234,
        mqChannelName = "apprec-mogateway-channel-name",
        mqQueuename = "apprec-queuename",
    ),
    pdlClientId = "pdlClientId",
    pdlUrl = pdlUrl ?: "http://pdl",
    motebehovUrl = "moetebehovUrl",
    oppfolgingsplanUrl = "oppfolgingsplanUrl",
)
