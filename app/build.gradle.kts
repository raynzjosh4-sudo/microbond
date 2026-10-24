plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.microbond.app"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.microbond.app"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // Render Staging: 24/7 backend sandbox
            buildConfigField("String", "BASE_URL", "\"https://microbond-backent.onrender.com/\"")
            buildConfigField("String", "WS_BASE_URL", "\"wss://microbond-backent.onrender.com/ws/\"")
            buildConfigField("Boolean", "ENABLE_HTTP_LOGS", "true")
        }
        release {
            optimization {
                enable = false
            }
            // Production Custom Domain / Cloud Run
            buildConfigField("String", "BASE_URL", "\"https://api.getmicrobond.com/\"")
            buildConfigField("String", "WS_BASE_URL", "\"wss://api.getmicrobond.com/ws/\"")
            buildConfigField("Boolean", "ENABLE_HTTP_LOGS", "false")
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
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("br.com.devsrsouza.compose.icons:tabler-icons:1.1.1")
    implementation(libs.androidx.ui.graphics)
    implementation(libs.country.code.picker)
    implementation(libs.lottie.compose)

    // Retrofit & Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
