plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    packagingOptions {
        pickFirst("lib/x86/libc++_shared.so")
        pickFirst("lib/x86_64/libc++_shared.so")
        pickFirst("lib/armeabi-v7a/libc++_shared.so")
        pickFirst("lib/arm64-v8a/libc++_shared.so")
    }
    namespace = "com.example.particleapped"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.particleapped"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        manifestPlaceholders["PN_PROJECT_ID"] = "60d61ff6-c038-42b7-bbfe-4bdc0a47b58f"
        manifestPlaceholders["PN_PROJECT_CLIENT_KEY"] = "cl5dtJzlLxcMf49PXgbNg845qOQHKGgSzKz1tuWz"
        manifestPlaceholders["PN_APP_ID"] = "f4bbf4db-03bf-4e16-9a2a-493aa8e0ba99"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    modules {
        module("org.bouncycastle:bcprov-jdk15to18") {
            replacedBy("org.bouncycastle:bcprov-jdk15on")
        }
    }
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // navController
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // particle network
    implementation("network.particle:auth-service:1.4.1")
    implementation("network.particle:api-service:1.4.1")

    // coil
    implementation("io.coil-kt:coil-compose:2.0.0-rc01")

    // qr code
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.google.guava:guava:31.0.1-android")

    // CameraX
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")

    // Zxing
    implementation("com.google.zxing:core:3.4.1")

    // json serializer
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    // web3j library
    implementation("org.web3j:core:4.8.4")

    // ktor
    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-client-cio:2.3.2")
    implementation("io.ktor:ktor-client-json:1.6.5")
    implementation("io.ktor:ktor-client-serialization:1.6.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    // blinkid for kyc verification
    implementation("com.microblink:blinkid:6.5.1@aar") {
        isTransitive = true
    }

    // ekyc for liveliness detection and face capture
//
    // face detection
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.yoti.mobile.android:face-capture-unbundled:4.2.1")

    implementation(platform("com.google.firebase:firebase-bom:29.0.0"))
}