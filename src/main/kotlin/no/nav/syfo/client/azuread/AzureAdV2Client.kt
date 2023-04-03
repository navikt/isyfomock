package no.nav.syfo.client.azuread

import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import no.nav.syfo.client.httpClientProxy
import org.slf4j.LoggerFactory

class AzureAdV2Client(
    private val aadAppClient: String,
    private val aadAppSecret: String,
    private val aadTokenEndpoint: String,
) {
    private val httpClient = httpClientProxy()

    @Volatile
    private var tokenMap = HashMap<String, AzureAdV2Token>()

    suspend fun getSystemToken(scopeClientId: String): AzureAdV2Token? {
        val cachedToken: AzureAdV2Token? = tokenMap[scopeClientId]

        return if (cachedToken == null || cachedToken.isExpired()) {
            val azureAdTokenResponse = getAccessToken(
                Parameters.build {
                    append("client_id", aadAppClient)
                    append("client_secret", aadAppSecret)
                    append("grant_type", "client_credentials")
                    append("scope", "api://$scopeClientId/.default")
                },
            )

            azureAdTokenResponse?.let { token ->
                val azureAdToken = token.toAzureAdV2Token()
                tokenMap[scopeClientId] = azureAdToken
                COUNT_CALL_AZUREAD_TOKEN_SYSTEM_CACHE_MISS.increment()
                azureAdToken
            }
        } else {
            COUNT_CALL_AZUREAD_TOKEN_SYSTEM_CACHE_HIT.increment()
            cachedToken
        }
    }

    private suspend fun getAccessToken(formParameters: Parameters): AzureAdV2TokenResponse? {
        return try {
            val response: HttpResponse = httpClient.post(aadTokenEndpoint) {
                accept(ContentType.Application.Json)
                setBody(FormDataContent(formParameters))
            }
            response.body<AzureAdV2TokenResponse>()
        } catch (e: ClientRequestException) {
            handleUnexpectedResponseException(e)
        } catch (e: ServerResponseException) {
            handleUnexpectedResponseException(e)
        }
    }

    private fun handleUnexpectedResponseException(
        responseException: ResponseException,
    ): AzureAdV2TokenResponse? {
        log.error(
            "Error while requesting AzureAdAccessToken with statusCode=${responseException.response.status.value}",
            responseException,
        )
        return null
    }

    companion object {
        private val log = LoggerFactory.getLogger(AzureAdV2Client::class.java)
    }
}
