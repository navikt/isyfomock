package no.nav.syfo.client.pdl

data class PdlHentIdenterResponse(
    val errors: List<PdlError>?,
    val data: PdlHentIdenter,
)

data class PdlHentIdenter(
    val hentIdenter: PdlIdenter,
)

data class PdlIdenter(
    val identer: List<PdlIdent>,
)
data class PdlIdent(
    val ident: String,
    val historisk: Boolean,
    val gruppe: String,
)

data class PdlError(
    val message: String,
    val locations: List<PdlErrorLocation>,
    val path: List<String>?,
    val extensions: PdlErrorExtension
)

data class PdlErrorLocation(
    val line: Int?,
    val column: Int?
)

data class PdlErrorExtension(
    val code: String?,
    val classification: String
)

fun PdlError.errorMessage(): String {
    return "${this.message} with code: ${extensions.code} and classification: ${extensions.classification}"
}
