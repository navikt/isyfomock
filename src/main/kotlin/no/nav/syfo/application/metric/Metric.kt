package no.nav.syfo.application.metric

import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry

const val METRICS_NS = "isyfomock"

val METRICS_REGISTRY = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
