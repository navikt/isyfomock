package no.nav.syfo.motebehov

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.syfo.client.httpClientDefault
import no.nav.syfo.client.pdl.PdlClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MotebehovService(
    private val motebehovUrl: String,
    private val pdlClient: PdlClient,
    private val httpClient: HttpClient = httpClientDefault(),
    private val logger: Logger = LoggerFactory.getLogger(MotebehovService::class.java)
) {
    suspend fun deleteMotebehov(fnr: String): String {
        val aktorId = pdlClient.fetchPdlAktorIdFraFnr(fnr)?.first()?.ident ?: throw IllegalArgumentException()

        val response = httpClient.delete("$motebehovUrl/internal/nullstill/$aktorId")

        return when (response.status) {
            HttpStatusCode.OK -> {
                response.body<String>().toString()
            }
            else -> {
                val errorMessage = "Unable to delete motebehov: ${response.status.value}"
                logger.error(errorMessage)
                errorMessage
            }
        }
    }
}
