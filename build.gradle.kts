// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    id("com.tddworks.central-portal-publisher") version "0.0.5"
    `java-library`
    `maven-publish`
    signing
}



java {
    withJavadocJar()
    withSourcesJar()
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

signing {
    useGpgCmd()
    sign(configurations.runtimeElements.get())
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            group = "co.dfns"
            version = "0.1.0"
            artifactId = "androidsdk"
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name = "Dfns Android SDK"
                description = "Dfns Passkey Android SDK"
                url = "https://github.com/dfns/dfns-sdk-kotlin"
                licenses {
                    license {
                        name = "MIT License"
                        url = "https://github.com/dfns/dfns-sdk-kotlin/blob/m/LICENSE"
                    }
                }
                developers {
                    developer {
                        id = "dfns"
                        name = "Dfns"
                        email = "john.doe@example.com"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/dfns/dfns-sdk-kotlin.git"
                    developerConnection = "scm:git:ssh://github.com/dfns/dfns-sdk-kotlin.git"
                    url = "https://github.com/dfns/dfns-sdk-kotlin/"
                }
            }
        }
    }
}

sonatypePortalPublisher {
    settings {
        autoPublish = false
        aggregation = false
    }
}
