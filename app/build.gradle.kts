
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.chamberlyab"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.chamberlyab"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.github.bumptech.glide:glide:5.0.0-rc01")

    implementation("com.google.firebase:firebase-ai")
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation("com.google.firebase:firebase-appcheck-playintegrity:18.0.0")
    runtimeOnly("com.google.gms:google-services:4.4.2")
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("com.google.firebase:firebase-bom:33.9.0")
    implementation("com.google.firebase:firebase-firestore:25.1.2")
    implementation ("com.google.firebase:firebase-storage:20.3.0")
    implementation ("com.google.firebase:firebase-appcheck-debug:17.1.0")
    implementation("com.google.ai.client.generativeai:generativeai:0.1.1")

    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.code.gson:gson:2.13.1")

    implementation("com.google.firebase:firebase-vertexai:16.1.0")
    implementation ("io.noties.markwon:core:4.6.2")



}