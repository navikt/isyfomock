package no.nav.syfo

data class Environment(
    val applicationPort: Int = getEnvVar("APPLICATION_PORT", "8080").toInt(),
    val mq: EnvironmentMQ = EnvironmentMQ(
        mqQueueManager = getEnvVar("MQGATEWAY_NAME"),
        mqHostname = getEnvVar("MQGATEWAY_HOSTNAME"),
        mqApplicationName = "isyfomock",
        mqPort = getEnvVar("MQGATEWAY_PORT", "1413").toInt(),
        mqChannelName = getEnvVar("MQGATEWAY_CHANNEL_NAME"),
        padm2Queuename = getEnvVar("PADM2_QUEUENAME"),
    ),
)

data class EnvironmentMQ(
    val mqQueueManager: String,
    val mqHostname: String,
    val mqApplicationName: String,
    val mqPort: Int,
    val mqChannelName: String,
    val padm2Queuename: String,
)

data class ServiceUser(
    val username: String = getEnvVar("SERVICEUSER_USERNAME"),
    val password: String = getEnvVar("SERVICEUSER_PASSWORD"),
)

fun getEnvVar(varName: String, defaultValue: String? = null) =
    System.getenv(varName) ?: defaultValue ?: throw RuntimeException("Missing required variable \"$varName\"")
