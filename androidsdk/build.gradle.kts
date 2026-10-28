plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    `maven-publish`
}

group = "co.dfns"
version = "1.0.0"

android {
    namespace = "co.dfns.androidsdk"
    compileSdk = 34

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
    testOptions {
        unitTests {
            // Safety net if a unit test ever touches an Android stub; the current tests are pure JVM.
            isReturnDefaultValues = true
        }
    }
}

// AGP aligns the unit test compile classpath with the test runtime classpath, which carries the newer
// androidx.credentials and its newer Kotlin stdlib. The test sources only need to compile, so skip
// the metadata version check there. The SDK itself still compiles against the minimum version.
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    if (name.contains("UnitTest")) {
        kotlinOptions.freeCompilerArgs += "-Xskip-metadata-version-check"
    }
}

dependencies {
    implementation(libs.androidx.credentials)
    implementation(libs.gson)

    testImplementation(libs.junit)
    // Run unit tests against the newest supported androidx.credentials (see PasskeyRequestCompatibilityTest).
    testRuntimeOnly(libs.androidx.credentials.latest)
    // Real org.json for unit tests: androidx.credentials parses request JSON with it, and the Android stub returns empty values.
    testRuntimeOnly(libs.org.json)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            artifactId = "androidsdk"
            afterEvaluate {
                from(components["release"])
            }
            pom {
                name = "Dfns Android SDK"
                description = "Dfns passkey signer for Android"
                url = "https://github.com/dfns/dfns-sdk-kotlin"
                licenses {
                    license {
                        name = "MIT License"
                        url = "https://github.com/dfns/dfns-sdk-kotlin/blob/main/LICENSE"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/dfns/dfns-sdk-kotlin.git"
                    developerConnection = "scm:git:ssh://github.com/dfns/dfns-sdk-kotlin.git"
                    url = "https://github.com/dfns/dfns-sdk-kotlin"
                }
            }
        }
    }
}
