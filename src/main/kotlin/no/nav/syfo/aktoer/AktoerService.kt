package no.nav.syfo.aktoer

import no.nav.syfo.client.pdl.PdlClient

class AktoerService(private val pdlClient: PdlClient) {
    suspend fun getSsnFormAktoerId(ssn: String): String? {
        return pdlClient.fetchPdlFnrFraAktoer(ssn)
    }

    suspend fun getAktoerIdFormSsn(aktoerId: String): String? {
        return pdlClient.fetchPdlFnrFraAktoer(aktoerId)
    }
}
