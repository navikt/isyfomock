package no.nav.syfo.aktoer

import no.nav.syfo.client.pdl.PdlClient

class AktoerService(private val pdlClient: PdlClient) {
    suspend fun getFnrFormAktoerId(aktoerId: String): String? {
        return pdlClient.fetchPdlFnrFraAktoerId(aktoerId)
    }

    suspend fun getAktoerIdFormFnr(fnr: String): String? {
        return pdlClient.fetchPdlAktoerIdFraFnr(fnr)
    }
}
