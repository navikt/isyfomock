group = "no.nav.syfo"
version = "0.0.1"

val jaxbVersion = "2.3.1"
val kithApprecVersion = "2019.09.09-08-50-693492ddc1d3f98e70c1638c94dcb95a66036d12"
val ktorVersion = "2.3.12"
val kluentVersion = "1.73"
val mockkVersion = "1.13.12"
val jacksonDataTypeVersion = "2.17.2"
val javaTimeAdapterVersion = "1.1.3"
val jsonVersion = "20231013"
val logbackVersion = "1.5.6"
val logstashEncoderVersion = "7.4"
val micrometerRegistryVersion = "1.12.8"
val mqVersion = "9.4.0.0"
val spekVersion = "2.0.19"
val syfotjenesterVersion = "1.2021.06.09-13.09-b3d30de9996e"
val swaggerUiVersion = "5.17.2"
val kafkaVersion = "3.8.0"

plugins {
    kotlin("jvm") version "1.9.24"
    id("com.gradleup.shadow") version "8.3.2"
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
    id("org.hidetake.swagger.generator") version "2.19.2" apply true
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashEncoderVersion")
    implementation("org.json:json:$jsonVersion")

    // Metrics and Prometheus
    implementation("io.ktor:ktor-server-metrics-micrometer:$ktorVersion")
    implementation("io.micrometer:micrometer-registry-prometheus:$micrometerRegistryVersion")

    // (De-)serialization
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonDataTypeVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonDataTypeVersion")
    implementation("javax.xml.bind:jaxb-api:$jaxbVersion")
    implementation("org.glassfish.jaxb:jaxb-runtime:$jaxbVersion")
    implementation("com.migesok:jaxb-java-time-adapters:$javaTimeAdapterVersion")

    // MQ
    implementation("com.ibm.mq:com.ibm.mq.allclient:$mqVersion")

    implementation("no.nav.syfotjenester:fellesformat:$syfotjenesterVersion")
    implementation("no.nav.syfotjenester:kith-base64:$syfotjenesterVersion")
    implementation("no.nav.syfotjenester:kith-dialogmelding:$syfotjenesterVersion")
    implementation("no.nav.syfotjenester:kith-hodemelding:$syfotjenesterVersion")
    implementation("no.nav.helse.xml:kith-apprec:$kithApprecVersion")

    // Kafka
    val excludeLog4j = fun ExternalModuleDependency.() {
        exclude(group = "log4j")
    }
    implementation("org.apache.kafka:kafka_2.13:$kafkaVersion", excludeLog4j)

    swaggerUI("org.webjars:swagger-ui:$swaggerUiVersion")

    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.amshove.kluent:kluent:$kluentVersion")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion") {
        exclude(group = "org.jetbrains.kotlin")
    }
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion") {
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
