import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

// Cargar local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.example.makip"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.makip"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Inyectar la API Key desde local.properties
        val imageKitPrivateKey = localProperties.getProperty("IMAGEKIT_PRIVATE_KEY") ?: ""
        buildConfigField("String", "IMAGEKIT_PRIVATE_KEY", "\"$imageKitPrivateKey\"")
        buildConfigField("String", "IMAGEKIT_URL_ENDPOINT", "\"https://ik.imagekit.io/aona7wwhs/\"")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation("androidx.media3:media3-ui:1.4.1")
    
    // Gson para persistencia de datos
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Coil para cargar imágenes
    implementation("io.coil-kt:coil:2.4.0")

    // Room Database
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    // OkHttp para subir imágenes
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Lifecycle / ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
}
