package no.nav.syfo.moetebehov

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import no.nav.syfo.client.httpClientDefault
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MoetebehovService(
    private val moetebehovUrl: String,
    private val httpClient: HttpClient = httpClientDefault(),
    private val logger: Logger = LoggerFactory.getLogger(MoetebehovService::class.java)
) {
    suspend fun deleteMoetebehov(fnr: String?) {
        val response = httpClient.delete("$moetebehovUrl/internal/nullstill/$fnr")

        when (response.status) {
            HttpStatusCode.OK -> {
                response.body<Any>().toString()
            }
            else -> {
                logger.error("Unable to delete moetebehov: ${response.status.value}")
            }
        }
    }
}
