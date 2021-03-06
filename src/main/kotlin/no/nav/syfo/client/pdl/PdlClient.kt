package no.nav.syfo.client.pdl

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import no.nav.syfo.client.azuread.AzureAdV2Client
import no.nav.syfo.client.httpClientDefault
import no.nav.syfo.metric.COUNT_CALL_PDL_FAIL
import no.nav.syfo.metric.COUNT_CALL_PDL_SUCCESS
import org.slf4j.LoggerFactory
import java.util.*

class PdlClient(
    private val azureAdV2Client: AzureAdV2Client,
    private val pdlClientId: String,
    private val pdlUrl: String,
) {
    private val NAV_CALL_ID_HEADER = "Nav-Call-Id"
    private val TEMA_HEADER = "Tema"
    private val ALLE_TEMA_HEADERVERDI = "GEN"

    enum class IdentGruppe {
        FOLKEREGISTERIDENT,
        AKTORID,
    }

    private val httpClient = httpClientDefault()

    suspend fun fetchPdlSsnFraAktoerId(aktoerId: String): String? {
        return fetchPdlIdenter(aktoerId, IdentGruppe.FOLKEREGISTERIDENT)
    }

    suspend fun fetchPdlAktoerIdFraSsn(ssn: String): String? {
        return fetchPdlIdenter(ssn, IdentGruppe.AKTORID)
    }

    suspend fun fetchPdlIdenter(identId: String, identType: IdentGruppe): String? {
        val query =
            """
            query(${'$'}ident: ID!) {
                hentIdenter(ident: ${'$'}ident, grupper: [$identType], historikk: false) {
                    identer {
                        ident
                        historisk
                        gruppe
                    }
                }
            }
            """.trimIndent()

        val request = PdlRequest(query, Variables(identId))

        val response: HttpResponse = httpClient.post(pdlUrl) {
            setBody(request)
            header(HttpHeaders.ContentType, "application/json")
            header(HttpHeaders.Accept, "application/json")
            header(HttpHeaders.Authorization, "Bearer ${azureAdV2Client.getSystemToken(pdlClientId)!!.accessToken}")
            header(HttpHeaders.XCorrelationId, UUID.randomUUID().toString())
            header(TEMA_HEADER, ALLE_TEMA_HEADERVERDI)
            header(NAV_CALL_ID_HEADER, UUID.randomUUID().toString())
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                val pdlPersonReponse = response.body<PdlHentIdenterResponse>()
                return if (pdlPersonReponse.errors != null && pdlPersonReponse.errors.isNotEmpty()) {
                    COUNT_CALL_PDL_FAIL.increment()
                    pdlPersonReponse.errors.forEach {
                        logger.error("Error while requesting person from PersonDataLosningen: ${it.errorMessage()}")
                    }
                    null
                } else {
                    COUNT_CALL_PDL_SUCCESS.increment()
                    pdlPersonReponse.data.hentIdenter.identer.toString()
                }
            }
            else -> {
                COUNT_CALL_PDL_FAIL.increment()
                logger.error("Request with url: $pdlUrl failed with reponse code ${response.status.value}")
                return null
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(PdlClient::class.java)
    }
}
