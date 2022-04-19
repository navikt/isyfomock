group = "no.nav.syfo"
version = "0.0.1"

object Versions {
    const val jaxb = "2.3.1"
    const val ktor = "2.0.0"
    const val kluent = "1.68"
    const val mockk = "1.12.3"
    const val jackson = "2.13.1"
    const val javaTimeAdapter = "1.1.3"
    const val logback = "1.2.11"
    const val logstashEncoder = "7.0.1"
    const val micrometerRegistry = "1.8.4"
    const val mq = "9.2.5.0"
    const val spek = "2.0.18"
    const val syfotjenester = "1.2021.06.09-13.09-b3d30de9996e"
    const val swaggerUi = "4.9.1"
}

plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
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
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${Versions.jackson}")
    implementation("javax.xml.bind:jaxb-api:${Versions.jaxb}")
    implementation("org.glassfish.jaxb:jaxb-runtime:${Versions.jaxb}")
    implementation("com.migesok:jaxb-java-time-adapters:${Versions.javaTimeAdapter}")

    // MQ
    implementation("com.ibm.mq:com.ibm.mq.allclient:${Versions.mq}")

    implementation("no.nav.syfotjenester:fellesformat:${Versions.syfotjenester}")
    implementation("no.nav.syfotjenester:kith-base64:${Versions.syfotjenester}")
    implementation("no.nav.syfotjenester:kith-dialogmelding:${Versions.syfotjenester}")
    implementation("no.nav.syfotjenester:kith-hodemelding:${Versions.syfotjenester}")

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

tasks {
    withType<Jar> {
        manifest.attributes["Main-Class"] = "no.nav.syfo.ApplicationKt"
    }

    create("printVersion") {
        doLast {
            println(project.version)
        }
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
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
