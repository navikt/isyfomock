package no.nav.syfo.aktor

import no.nav.syfo.client.pdl.PdlClient
import no.nav.syfo.client.pdl.PdlIdent

class AktorService(private val pdlClient: PdlClient) {
    suspend fun getFnrFromAktorId(aktorId: String): List<PdlIdent>? {
        return pdlClient.fetchPdlFnrFraAktorId(aktorId)
    }

    suspend fun getAktorIdFromFnr(fnr: String): List<PdlIdent>? {
        return pdlClient.fetchPdlAktorIdFraFnr(fnr)
    }
}
