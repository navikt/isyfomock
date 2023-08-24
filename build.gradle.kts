group = "no.nav.syfo"
version = "0.0.1"

object Versions {
    const val jaxb = "2.3.1"
    const val kithApprecVersion = "2019.07.30-04-23-2a0d1388209441ec05d2e92a821eed4f796a3ae2"
    const val ktor = "2.3.2"
    const val kluent = "1.73"
    const val mockk = "1.13.5"
    const val jacksonDataType = "2.15.2"
    const val javaTimeAdapter = "1.1.3"
    const val logback = "1.4.7"
    const val logstashEncoder = "7.3"
    const val micrometerRegistry = "1.11.2"
    const val mq = "9.3.3.0"
    const val spek = "2.0.19"
    const val syfotjenester = "1.2021.06.09-13.09-b3d30de9996e"
    const val swaggerUi = "5.1.3"
    const val kafka = "3.5.1"
}

plugins {
    kotlin("jvm") version "1.9.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.jlleitschuh.gradle.ktlint") version "11.4.2"
    id("org.hidetake.swagger.generator") version "2.19.2" apply true
}

val githubUser: String by project
val githubPassword: String by project
repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/navikt/syfotjenester")
        credentials {
            username = githubUser
            password = githubPassword
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation("io.ktor:ktor-serialization-jackson:${Versions.ktor}")
    implementation("io.ktor:ktor-client-apache:${Versions.ktor}")
    implementation("io.ktor:ktor-client-content-negotiation:${Versions.ktor}")
    implementation("io.ktor:ktor-client-cio:${Versions.ktor}")
    implementation("io.ktor:ktor-server-content-negotiation:${Versions.ktor}")
    implementation("io.ktor:ktor-server-status-pages:${Versions.ktor}")
    implementation("io.ktor:ktor-server-netty:${Versions.ktor}")

    // Logging
    implementation("ch.qos.logback:logback-classic:${Versions.logback}")
    implementation("net.logstash.logback:logstash-logback-encoder:${Versions.logstashEncoder}")

    // Metrics and Prometheus
    implementation("io.ktor:ktor-server-metrics-micrometer:${Versions.ktor}")
    implementation("io.micrometer:micrometer-registry-prometheus:${Versions.micrometerRegistry}")

    // (De-)serialization
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${Versions.jacksonDataType}")
    implementation("javax.xml.bind:jaxb-api:${Versions.jaxb}")
    implementation("org.glassfish.jaxb:jaxb-runtime:${Versions.jaxb}")
    implementation("com.migesok:jaxb-java-time-adapters:${Versions.javaTimeAdapter}")

    // MQ
    implementation("com.ibm.mq:com.ibm.mq.allclient:${Versions.mq}")

    implementation("no.nav.syfotjenester:fellesformat:${Versions.syfotjenester}")
    implementation("no.nav.syfotjenester:kith-base64:${Versions.syfotjenester}")
    implementation("no.nav.syfotjenester:kith-dialogmelding:${Versions.syfotjenester}")
    implementation("no.nav.syfotjenester:kith-hodemelding:${Versions.syfotjenester}")
    implementation("no.nav.helse.xml:kith-apprec:${Versions.kithApprecVersion}")

    // Kafka
    val excludeLog4j = fun ExternalModuleDependency.() {
        exclude(group = "log4j")
    }
    implementation("org.apache.kafka:kafka_2.13:${Versions.kafka}", excludeLog4j)

    swaggerUI("org.webjars:swagger-ui:${Versions.swaggerUi}")

    testImplementation("io.ktor:ktor-server-test-host:${Versions.ktor}")
    testImplementation("io.mockk:mockk:${Versions.mockk}")
    testImplementation("org.amshove.kluent:kluent:${Versions.kluent}")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:${Versions.spek}") {
        exclude(group = "org.jetbrains.kotlin")
    }
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:${Versions.spek}") {
        exclude(group = "org.jetbrains.kotlin")
    }
}

swaggerSources {
    create("isyfomock").apply {
        setInputFile(file("api/oas3/isyfomock-api.yaml"))
    }
}

kotlin {
    jvmToolchain(17)
}

tasks {
    withType<Jar> {
        manifest.attributes["Main-Class"] = "no.nav.syfo.ApplicationKt"
    }

    create("printVersion") {
        doLast {
            println(project.version)
        }
    }

    withType<org.hidetake.gradle.swagger.generator.GenerateSwaggerUI> {
        outputDir = File(buildDir.path + "/resources/main/api")
    }

    withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
        archiveBaseName.set("app")
        archiveClassifier.set("")
        archiveVersion.set("")
        dependsOn("generateSwaggerUI")
    }

    withType<Test> {
        useJUnitPlatform {
            includeEngines("spek2")
        }
        testLogging.showStandardStreams = true
    }
}
