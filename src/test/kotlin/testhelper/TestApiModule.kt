package testhelper

import io.ktor.server.application.*
import io.mockk.mockk
import no.nav.syfo.application.ApplicationState
import no.nav.syfo.application.api.apiModule
import no.nav.syfo.application.cache.RedisStore
import no.nav.syfo.mq.MQSender
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.Protocol

fun Application.testApiModule(
    applicationState: ApplicationState = ApplicationState(alive = true, ready = true),
    mqSender: MQSender = mockk()
) {
    val testEnv = testEnvironment()
    val cache = RedisStore(
        JedisPool(
            JedisPoolConfig(),
            testEnv.redisHost,
            testEnv.redisPort,
            Protocol.DEFAULT_TIMEOUT,
            testEnv.redisSecret
        )
    )

    this.apiModule(applicationState = applicationState, mqSender = mqSender, cache = cache, environment = testEnv)
}
