package no.nav.syfo.application.api

import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Route.registerSwaggerDocApi() {
    route("/api/v1/docs/") {
        static {
            resources("api")
            defaultResource("api/index.html")
        }
    }
}
