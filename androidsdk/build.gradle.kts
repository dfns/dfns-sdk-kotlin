plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

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
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.gson)

    implementation("androidx.credentials:credentials:1.2.2")
//    implementation("androidx.credentials:credentials-play-services-auth:1.2.2")

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    // Force a patched Bouncy Castle on the (test-only) Robolectric classpath: Robolectric 4.16.1
    // pulls bcprov-jdk18on:1.81, which is affected by CVE-2026-58062 (critical) and
    // GHSA-qp49-qgx5-5m26 (high). It never ships in the SDK, but keep the CI dependency graph clean.
    constraints {
        testImplementation(libs.bouncycastle.bcprov) {
            because("bcprov-jdk18on < 1.82 has CVE-2026-58062 / GHSA-qp49-qgx5-5m26")
        }
    }
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}