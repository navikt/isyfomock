package no.nav.syfo.aktoer

import no.nav.syfo.client.pdl.PdlClient

class AktoerService(private val pdlClient: PdlClient) {
    suspend fun getSsnFormAktoerId(aktoerId: String): String? {
        return pdlClient.fetchPdlSsnFraAktoerId(aktoerId)
    }

    suspend fun getAktoerIdFormSsn(ssn: String): String? {
        return pdlClient.fetchPdlAktoerIdFraSsn(ssn)
    }
}
