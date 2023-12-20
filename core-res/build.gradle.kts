plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}


android {
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }
    kapt {
        correctErrorTypes = true
    }
    namespace = "com.tenfingers.core_res"
}

dependencies {

    //kotlin
    implementation(Libraries.KTX.CORE)
    implementation(Libraries.AndroidX.LIFECYCLE_RUNTIME)
    implementation(Libraries.Compose.COMPOSE_ACTIVITY)
    implementation(Libraries.AndroidX.APP_COMPAT)

    implementation(platform(Libraries.Compose.COMPOSE_BOM))
    implementation(Libraries.Compose.COMPOSE_UI)
    implementation(Libraries.Compose.COMPOSE_UI_GRAPHICS)
    implementation(Libraries.Compose.COMPOSE_UI_TOOLING_PREVIEW)
    implementation(Libraries.Compose.COMPOSE_UI_MATERIAL3)
    implementation(Libraries.Compose.COMPOSE_HILT_NAVIGATION)
    kapt(Libraries.Compose.COMPOSE_COMPILER)

    implementation(Libraries.Hilt.ANDROID)
    kapt(Libraries.Hilt.COMPILER_KAPT)

    testImplementation(Libraries.Test.JUNIT)
    androidTestImplementation(Libraries.AndroidTest.ANDROID_JUNIT)
    androidTestImplementation(Libraries.AndroidTest.ESPRESSO)
    androidTestImplementation(platform(Libraries.Compose.COMPOSE_BOM))
    androidTestImplementation(Libraries.Compose.COMPOSE_ANDROID_TEST)
    debugImplementation(Libraries.Compose.COMPOSE_DEBUG_UI_TOOLING)
    debugImplementation(Libraries.Compose.COMPOSE_DEBUG_UI_TEST_MANIFEST)
}