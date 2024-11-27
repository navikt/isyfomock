package no.nav.syfo

import no.nav.syfo.kafka.KafkaEnvironment

data class Environment(
    val applicationPort: Int = getEnvVar("APPLICATION_PORT", "8080").toInt(),
    val aadAppClient: String = getEnvVar("AZURE_APP_CLIENT_ID"),
    val aadAppSecret: String = getEnvVar("AZURE_APP_CLIENT_SECRET"),
    val aadTokenEndpoint: String = getEnvVar("AZURE_OPENID_CONFIG_TOKEN_ENDPOINT"),
    val mq: EnvironmentMQ = EnvironmentMQ(
        mqQueueManager = getEnvVar("MQGATEWAY_NAME"),
        mqHostname = getEnvVar("MQGATEWAY_HOSTNAME"),
        mqApplicationName = "isyfomock",
        mqPort = getEnvVar("MQGATEWAY_PORT", "1413").toInt(),
        mqChannelName = getEnvVar("MQGATEWAY_CHANNEL_NAME"),
        mqQueuename = getEnvVar("PADM2_QUEUENAME"),
    ),
    val apprecMQ: EnvironmentMQ = EnvironmentMQ(
        mqQueueManager = getEnvVar("APPREC_MQGATEWAY_NAME"),
        mqHostname = getEnvVar("APPREC_MQGATEWAY_HOSTNAME"),
        mqApplicationName = "isyfomock",
        mqPort = getEnvVar("APPREC_MQGATEWAY_PORT", "1413").toInt(),
        mqChannelName = getEnvVar("APPREC_MQGATEWAY_CHANNEL_NAME"),
        mqQueuename = getEnvVar("APPREC_QUEUENAME"),
    ),
    val pdlUrl: String = getEnvVar("PDL_URL"),
    val pdlClientId: String = getEnvVar("PDL_CLIENT_ID"),
    val motebehovUrl: String = getEnvVar("SYFOMOTEBEHOV_URL"),
    val oppfolgingsplanUrl: String = getEnvVar("OPPFOLGINGSPLAN_URL"),
    val kafka: KafkaEnvironment = KafkaEnvironment(
        aivenBootstrapServers = getEnvVar("KAFKA_BROKERS"),
        aivenCredstorePassword = getEnvVar("KAFKA_CREDSTORE_PASSWORD"),
        aivenKeystoreLocation = getEnvVar("KAFKA_KEYSTORE_PATH"),
        aivenSecurityProtocol = "SSL",
        aivenTruststoreLocation = getEnvVar("KAFKA_TRUSTSTORE_PATH"),
        aivenSchemaRegistryUrl = getEnvVar("KAFKA_SCHEMA_REGISTRY"),
        aivenRegistryUser = getEnvVar("KAFKA_SCHEMA_REGISTRY_USER"),
        aivenRegistryPassword = getEnvVar("KAFKA_SCHEMA_REGISTRY_PASSWORD"),
    ),
)

data class EnvironmentMQ(
    val mqQueueManager: String,
    val mqHostname: String,
    val mqApplicationName: String,
    val mqPort: Int,
    val mqChannelName: String,
    val mqQueuename: String,
)

data class ServiceUser(
    val username: String = getEnvVar("SERVICEUSER_USERNAME"),
    val password: String = getEnvVar("SERVICEUSER_PASSWORD"),
)

fun getEnvVar(varName: String, defaultValue: String? = null) =
    System.getenv(varName) ?: defaultValue ?: throw RuntimeException("Missing required variable \"$varName\"")
