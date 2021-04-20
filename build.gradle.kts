import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("maven-publish")
}

repositories {
    mavenCentral()
}

val kotlinVersion = plugins.getPlugin(KotlinPluginWrapper::class.java).kotlinPluginVersion
val ktorVersion = findProperty("ktor_version")?.toString() ?: ""
val slf4jVersion = findProperty("slf4j_version")?.toString() ?: ""
val logVersion = findProperty("logback_version")?.toString()  ?: ""

dependencies {
    implementation(kotlin("stdlib", kotlinVersion))
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("io.ktor:ktor-metrics:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")

    implementation("io.ktor:ktor-jackson:$ktorVersion") // needed for parameter parsing and multipart parsing
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.8") // needed for multipart parsing
    implementation("org.webjars:swagger-ui:3.25.0")

    implementation("org.reflections:reflections:0.9.11") // only used while initializing

    testImplementation("io.ktor:ktor-server-netty:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("ch.qos.logback:logback-classic:$logVersion")
    testImplementation("io.ktor:ktor-auth:$ktorVersion")
    testImplementation("io.ktor:ktor-auth-jwt:$ktorVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xuse-experimental=kotlin.ExperimentalStdlibApi")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            group = "com.1gravity"
            artifactId = "ktor-openapi-generator"
            version = "0.2-beta.17"

            from(components["java"])

            pom {
                name.set("Ktor OpenAPI Generator")
                description.set("The Ktor OpenAPI Generator automatically generate the OpenAPI documentation based on the Ktor application routing definition")
                packaging = "jar"

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
            }
        }
    }
}
