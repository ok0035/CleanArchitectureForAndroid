plugins {
    id("kotlin-kapt")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
}
android {
    namespace = "com.zerodeg.app_video_editor"
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        applicationId = "com.zerodeg.app_video_editor"
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {

    implementation(project(Modules.CORE_DATA))
    implementation(project(Modules.CORE_DOMAIN))
    implementation(project(Modules.FEATURE_VIDEO))

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