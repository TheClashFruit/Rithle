import com.android.build.gradle.options.parseBoolean
import java.lang.Boolean.parseBoolean
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

fun gitHash(): String {
    return ProcessBuilder("git", "rev-parse", "--short", "HEAD")
        .start()
        .inputStream
        .bufferedReader()
        .readLine()
        ?.trim() ?: "unknown"
}

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) load(f.inputStream())
}

android {
    namespace = "me.theclashfruit.rithle"

    compileSdk {
        version = release(37) {
            minorApiLevel = 0
        }
    }

    defaultConfig {
        applicationId = "me.theclashfruit.rithle"
        minSdk = 28
        targetSdk = 37
        versionCode = 1
        versionName = "1.0.0-beta.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_MODRINTH", "\"${project.findProperty("api.modrinth") ?: ""}\"")
        buildConfigField("Boolean", "API_MODRINTH_LOCAL_OAUTH", "${project.findProperty("api.modrinth.localOAuth") ?: "false"}")
        buildConfigField("String", "API_RITHLE", "\"${project.findProperty("api.rithle") ?: ""}\"")

        buildConfigField("String", "CLIENT_ID", "\"${localProps.getProperty("client.id") ?: System.getenv("CLIENT_ID") ?: project.findProperty("api.modrinth.oauth.clientId") ?: ""}\"")
        buildConfigField("String", "CLIENT_SECRET", "\"${localProps.getProperty("client.secret") ?: System.getenv("CLIENT_ID") ?: ""}\"")

        buildConfigField("String", "GIT_HASH", "\"${gitHash()}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            versionNameSuffix = "-dev+${gitHash()}"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material3.window.size.class1)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)

    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.icons.lucide.cmp)

    implementation("com.mikepenz:multiplatform-markdown-renderer-android:0.41.0")
    implementation("com.mikepenz:multiplatform-markdown-renderer-m3:0.41.0")
    implementation("com.mikepenz:multiplatform-markdown-renderer-coil3:0.41.0")

    implementation("androidx.datastore:datastore-preferences:1.2.1")
    implementation("androidx.browser:browser:1.10.0")
    implementation(libs.androidx.documentfile)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.logging)

    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    implementation(libs.coil.network.ktor3)

    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}