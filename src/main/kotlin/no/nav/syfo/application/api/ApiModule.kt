package no.nav.syfo.application.api

import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.syfo.Environment
import no.nav.syfo.aktor.AktorService
import no.nav.syfo.aktor.registerAktorApi
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.metric.registerMetricApi
import no.nav.syfo.apprec.ApprecService
import no.nav.syfo.apprec.api.registerApprecApi
import no.nav.syfo.client.azuread.AzureAdV2Client
import no.nav.syfo.client.pdl.PdlClient
import no.nav.syfo.configureJacksonMapper
import no.nav.syfo.dialogmelding.DialogmeldingService
import no.nav.syfo.dialogmelding.api.registerDialogmeldingApi
import no.nav.syfo.esyfovarsel.EsyfovarselProducer
import no.nav.syfo.esyfovarsel.api.registerEsyfovarselApi
import no.nav.syfo.esyfovarsel.model.EsyfovarselHendelse
import no.nav.syfo.meroppfolging.SenOppfolgingSvarProducer
import no.nav.syfo.meroppfolging.SenOppfolgingVarselProducer
import no.nav.syfo.meroppfolging.api.registerMerOppfolgingApi
import no.nav.syfo.meroppfolging.model.SenOppfolgingSvar
import no.nav.syfo.meroppfolging.model.SenOppfolgingVarsel
import no.nav.syfo.motebehov.MotebehovService
import no.nav.syfo.motebehov.api.registerMotebehovApi
import no.nav.syfo.mq.MQSender
import no.nav.syfo.oppfolgingsplan.OppfolgingsplanService
import no.nav.syfo.oppfolgingsplan.registerOppfolgingsplanApi
import no.nav.syfo.reset.TestdataResetProducer
import no.nav.syfo.reset.api.registerTestdataResetApi
import org.apache.kafka.clients.producer.KafkaProducer

fun Application.apiModule(
    applicationState: ApplicationState,
    mqSender: MQSender,
    apprecMQSender: MQSender,
    environment: Environment,
    esyfovarselHendelseProducer: KafkaProducer<String, EsyfovarselHendelse>,
    senOppfolgingSvarProducer: KafkaProducer<String, SenOppfolgingSvar>,
    senOppfolgingVarselProducer: KafkaProducer<String, SenOppfolgingVarsel>,
    testdataResetKafkaProducer: KafkaProducer<String, String>,
) {
    install(ContentNegotiation) {
        jackson(block = configureJacksonMapper())
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.log.error("Caught exception", cause)

            val response: Pair<HttpStatusCode, String> = when (cause) {
                is ResponseException -> Pair(
                    cause.response.status,
                    cause.message ?: "Unknown error",
                )
                is IllegalArgumentException -> Pair(
                    HttpStatusCode.BadRequest,
                    cause.message ?: "Unknown error",
                )
                else -> Pair(
                    HttpStatusCode.InternalServerError,
                    "The server reported an unexpected error and cannot complete the request.",
                )
            }

            call.respond(response.first, response.second)
        }
    }

    val azureAdV2Client = AzureAdV2Client(
        aadAppClient = environment.aadAppClient,
        aadAppSecret = environment.aadAppSecret,
        aadTokenEndpoint = environment.aadTokenEndpoint,
    )

    val pdlClient = PdlClient(
        azureAdV2Client = azureAdV2Client,
        pdlClientId = environment.pdlClientId,
        pdlUrl = environment.pdlUrl,
    )

    val dialogmeldingService = DialogmeldingService(mqSender = mqSender)
    val apprecService = ApprecService(mqSender = apprecMQSender)
    val motebehovService = MotebehovService(motebehovUrl = environment.motebehovUrl, pdlClient = pdlClient)
    val aktorService = AktorService(pdlClient = pdlClient)
    val oppfolgingsplanService = OppfolgingsplanService(oppfolgingsplanUrl = environment.oppfolgingsplanUrl)
    val esyfovarselProducer = EsyfovarselProducer(kafkaProducer = esyfovarselHendelseProducer)
    val testdataResetProducer = TestdataResetProducer(kafkaProducer = testdataResetKafkaProducer)

    routing {
        registerPodApi(applicationState = applicationState)
        registerMetricApi()
        registerSwaggerDocApi()
        registerDialogmeldingApi(dialogmeldingService = dialogmeldingService)
        registerApprecApi(apprecService = apprecService)
        registerMotebehovApi(motebehovService = motebehovService)
        registerAktorApi(aktorService = aktorService)
        registerOppfolgingsplanApi(oppfolgingsplanService = oppfolgingsplanService)
        registerEsyfovarselApi(esyfovarselProducer = esyfovarselProducer)
        registerMerOppfolgingApi(
            svarProducer = SenOppfolgingSvarProducer(kafkaProducer = senOppfolgingSvarProducer),
            varselProducer = SenOppfolgingVarselProducer(kafkaProducer = senOppfolgingVarselProducer),
        )
        registerTestdataResetApi(producer = testdataResetProducer)
    }
}
