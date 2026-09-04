plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.bhplusplus.yaya"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.bhplusplus.yaya"
        minSdk = 26
        targetSdk = 37
        versionCode = 6
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // Force safe version of dependencies to mitigate CVEs
    constraints {
        implementation(libs.netty.codec.http2) {
            because("Fixes Netty HTTP/2 DoS and MadeYouReset DDoS vulnerabilities")
        }
        implementation(libs.netty.handler) {
            because("Fixes Netty SslHandler native crash and IPv6 Subnet Filter Bypass vulnerabilities")
        }
        implementation(libs.netty.codec.http) {
            because("Fixes Netty SpdyHttpDecoder ByteBuf leak and Request Smuggling vulnerabilities")
        }
        implementation(libs.netty.codec) {
            because("Fixes Netty Bzip2Decoder Infinite Loop and Lz4FrameDecoder vulnerabilities")
        }
        implementation(libs.netty.common) {
            because("Fixes Netty Windows App DoS vulnerabilities")
        }
        implementation(libs.netty.handler.proxy) {
            because("Fixes Netty HTTP Header Injection via HttpProxyHandler")
        }
        implementation(libs.bouncycastle.bcprov) {
            because("Fixes CVE-2024-34447: Bouncy Castle GOST 28147 CTR mode and LDAP injection")
        }
        implementation(libs.bouncycastle.bcpkix) {
            because("Fixes Bouncy Castle Crypto: Use of a Broken or Risky Cryptographic Algorithm")
        }
        implementation(libs.apache.httpclient) {
            because("Fixes CVE-2020-13956: Apache HttpClient misinterprets malformed authority")
        }
        implementation(libs.apache.commons.lang3) {
            because("Fixes Apache Commons Lang Uncontrolled Recursion vulnerability")
        }
        implementation(libs.jose4j) {
            because("Fixes jose4j DoS via compressed JWE content")
        }
        implementation(libs.google.guava) {
            because("Fixes Guava insecure use of temporary directory and Information Disclosure")
        }
    }

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.multiplatform.settings)
    implementation(libs.androidx.core.splashscreen)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.ui)

    // Supabase
    implementation(libs.supabase.postgrest.kt)
    implementation(libs.supabase.auth.kt)
    implementation(libs.supabase.realtime.kt)
    implementation(libs.supabase.storage.kt)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Ktor
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
