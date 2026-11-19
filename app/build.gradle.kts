plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.firebase.service)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

val selectedAiProject = providers.gradleProperty("plantcare.aiProject").orElse("plantcare").get()
require(selectedAiProject in setOf("plantcare-ia", "plantcare")) {
    "plantcare.aiProject must be plantcare-ia or plantcare; received: $selectedAiProject"
}

android {
    namespace = "plat.plantcare.app"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "plat.plantcare.app"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "GEMINI_MODEL", "\"${providers.gradleProperty("plantcare.geminiModel").orElse("gemini-3.8-flash").get()}\"")
        buildConfigField("String", "AI_PROJECT", "\"$selectedAiProject\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "USE_EMULATOR", providers.gradleProperty("plantcare.useEmulator").orElse("false").get())
            buildConfigField("String", "EMULATOR_HOST", "\"${providers.gradleProperty("plantcare.emulatorHost").orElse("10.0.2.2").get()}\"")
        }
        release {
            buildConfigField("boolean", "USE_EMULATOR", "false")
            buildConfigField("String", "EMULATOR_HOST", "\"\"")
            // ...your existing release config
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(libs.androidx.work.runtime)
    implementation(platform(libs.androidx.compose.bom))

    implementation(platform(libs.firebase.bom))
    implementation(libs.androidx.exifinterface)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.ai)
    debugImplementation(libs.firebase.appcheck.debug)
    releaseImplementation(libs.firebase.appcheck.playintegrity)

    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.compose.foundation.layout)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.androidx.compose.navigation)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.kotlinx.coroutines.play.services)

    implementation(libs.androidx.datastore)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.bundles.ktor)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}