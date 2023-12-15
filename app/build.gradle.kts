plugins {
    id("kotlin-kapt")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.zerodeg.cleanarchitecture"
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        applicationId = "com.zerodeg.cleanarchitecture"
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName

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

}

dependencies {

    implementation(project(Modules.CORE_DATA))
    implementation(project(Modules.CORE_DOMAIN))
    implementation(project(Modules.FEATURE_MAIN))

    //kotlin
    implementation(Libraries.KTX.CORE)
    implementation(Libraries.AndroidX.APP_COMPAT)

    //Compose
    implementation(Libraries.Compose.COMPOSE_ACTIVITY)
    implementation(platform(Libraries.Compose.COMPOSE_BOM))
    implementation(Libraries.Compose.COMPOSE_UI)
    implementation(Libraries.Compose.COMPOSE_UI_GRAPHICS)
    implementation(Libraries.Compose.COMPOSE_UI_TOOLING_PREVIEW)
    implementation(Libraries.Compose.COMPOSE_UI_MATERIAL3)
    implementation(Libraries.Compose.COMPOSE_UI_CONSTRAINTS)
    implementation(Libraries.Compose.COMPOSE_HILT_NAVIGATION)
    implementation(Libraries.Compose.COMPOSE_RUNTIME)
    implementation(Libraries.Compose.COMPOSE_FOUNDATION)
    kapt(Libraries.Compose.COMPOSE_COMPILER)

    //hilt
    implementation(Libraries.Hilt.ANDROID)
    kapt(Libraries.Hilt.COMPILER_KAPT)

    implementation(Libraries.Navigation.FRAGMENT_KTX)
    implementation(Libraries.Navigation.UI_KTX)

    testImplementation(Libraries.Test.JUNIT)
    androidTestImplementation(Libraries.AndroidTest.ANDROID_JUNIT)
    androidTestImplementation(Libraries.AndroidTest.ESPRESSO)
}