package no.nav.syfo.oppfolgingsplan

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.syfo.client.httpClientDefault
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class OppfolgingsplanService(
    private val oppfolgingsplanUrl: String,
    private val httpClient: HttpClient = httpClientDefault(),
    private val logger: Logger = LoggerFactory.getLogger(OppfolgingsplanService::class.java),
) {
    suspend fun deleteOppfolgingsplanerForArbeidstaker(fnr: String): String {
        val response = httpClient.delete("$oppfolgingsplanUrl/internal/oppfolgingsplan/slett/person/$fnr")

        return when (response.status) {
            HttpStatusCode.OK -> {
                response.body<Any>().toString()
            }
            else -> {
                val errorMessage = "Unable to delete oppfolgingsplan: ${response.status.value}"
                logger.error(errorMessage)
                errorMessage
            }
        }
    }

    suspend fun deleteOppfolgingsplan(oppfolgingsplanId: String): String {
        val response = httpClient.delete("$oppfolgingsplanUrl/internal/oppfolgingsplan/slett/$oppfolgingsplanId")

        return when (response.status) {
            HttpStatusCode.OK -> {
                response.body<Any>().toString()
            }
            else -> {
                val errorMessage = "Unable to delete oppfolgingsplan: ${response.status.value}"
                logger.error(errorMessage)
                errorMessage
            }
        }
    }
}
