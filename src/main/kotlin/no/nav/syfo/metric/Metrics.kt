package no.nav.syfo.metric

import io.micrometer.core.instrument.Counter
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry

const val METRICS_NS = "isyfomock"

val METRICS_REGISTRY = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

const val CALL_PDL_SUCCESS = "${METRICS_NS}_call_pdl_success_count"
const val CALL_PDL_FAIL = "${METRICS_NS}_call_pdl_fail_count"

val COUNT_CALL_PDL_SUCCESS: Counter = Counter.builder(CALL_PDL_SUCCESS)
    .description("Counts the number of successful calls to pdl")
    .register(METRICS_REGISTRY)
val COUNT_CALL_PDL_FAIL: Counter = Counter.builder(CALL_PDL_FAIL)
    .description("Counts the number of failed calls to pdl")
    .register(METRICS_REGISTRY)
