import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val appSecrets = Properties().apply {
    listOf("secrets.properties", "local.properties").forEach { fileName ->
        val file = rootProject.file(fileName)
        if (file.exists()) {
            file.inputStream().use { load(it) }
        }
    }
}

android {
    namespace = "com.vevaan.breakout"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.vevaan.breakout"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "SUPABASE_URL", "\"${appSecrets.getProperty("SUPABASE_URL", "")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${appSecrets.getProperty("SUPABASE_ANON_KEY", "")}\"")
        buildConfigField("String", "SPOTIFY_CLIENT_ID", "\"${appSecrets.getProperty("SPOTIFY_CLIENT_ID", "")}\"")
        buildConfigField("String", "SPOTIFY_CLIENT_SECRET", "\"${appSecrets.getProperty("SPOTIFY_CLIENT_SECRET", "")}\"")
        buildConfigField("String", "LASTFM_API_KEY", "\"${appSecrets.getProperty("LASTFM_API_KEY", "")}\"")
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
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
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("io.coil-kt:coil-compose:2.7.0")
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
