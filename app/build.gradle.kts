plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
}

android {
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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        viewBinding = true
    }
    namespace = "com.zerodeg.cleanarchitecture"
}

dependencies {

    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":feature-main"))

    implementation(Libraries.AndroidX.APP_COMPAT)
    implementation(Libraries.AndroidX.MARTERIAL)
    implementation(Libraries.AndroidX.CONSTRAINT_LAYOUT)

    //kotlin
    implementation(Libraries.KTX.CORE)

    //LifeCycle
    implementation(Libraries.KTX.FRAGMENT)

    //hilt
    implementation(Libraries.Hilt.ANDROID)
    kapt(Libraries.Hilt.COMPILER_KAPT)

    //retrofit
    implementation(Libraries.Retrofit.RETROFIT)
    implementation(Libraries.Retrofit.GSON)

    //okhttp
    implementation(Libraries.OkHttp.OKHTTP)
    implementation(Libraries.OkHttp.INTERCEPTER)

    //lottie
    implementation(Libraries.Lottie.LOTTIE)

    //glide
    implementation(Libraries.Glide.GLIDE)
    kapt(Libraries.Glide.COMPILER_KAPT)

    implementation (Libraries.Navigation.SAFE_ARGS_GRADLE_PLUGIN)
    implementation (Libraries.Navigation.FRAGMENT_KTX)
    implementation (Libraries.Navigation.UI_KTX)

    testImplementation(Libraries.Test.JUNIT)
    androidTestImplementation(Libraries.AndroidTest.ANDROID_JUNIT)
    androidTestImplementation(Libraries.AndroidTest.ESPRESSO)
}