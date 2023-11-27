plugins {
    id("kotlin-kapt")
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.zerodeg.feature_video"
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kapt {
        correctErrorTypes = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(project(Modules.CORE_DATA))
    implementation(project(Modules.CORE_DOMAIN))

    //kotlin
    implementation(Libraries.KTX.CORE)
    implementation(Libraries.KTX.FRAGMENT)

    //LifeCycle
    implementation(Libraries.AndroidX.LIFECYCLE_RUNTIME)
    implementation(Libraries.AndroidX.APP_COMPAT)
    implementation(Libraries.AndroidX.MARTERIAL)
    implementation(Libraries.AndroidX.CONSTRAINT_LAYOUT)


    implementation(Libraries.Compose.COMPOSE_ACTIVITY)
    implementation(platform(Libraries.Compose.COMPOSE_BOM))
    implementation(Libraries.Compose.COMPOSE_UI)
    implementation(Libraries.Compose.COMPOSE_UI_GRAPHICS)
    implementation(Libraries.Compose.COMPOSE_UI_TOOLING_PREVIEW)
    implementation(Libraries.Compose.COMPOSE_UI_MATERIAL3)
    kapt(Libraries.Compose.COMPOSE_COMPILER)

    //hilt
    implementation(Libraries.Hilt.ANDROID)
    kapt(Libraries.Hilt.COMPILER_KAPT)

    testImplementation(Libraries.Test.JUNIT)
    androidTestImplementation(Libraries.AndroidTest.ANDROID_JUNIT)
    androidTestImplementation(Libraries.AndroidTest.ESPRESSO)
}