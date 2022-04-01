package no.nav.syfo.application.api

import io.ktor.http.content.*
import io.ktor.routing.*

fun Route.registerSwaggerDocApi() {
    route("/api/v1/docs/") {
        static {
            resources("api")
            defaultResource("api/index.html")
        }
    }
}
