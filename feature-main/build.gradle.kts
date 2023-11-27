plugins {
    id("kotlin-kapt")
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.zerodeg.feature_main"
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
    buildFeatures {
        viewBinding = true
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

    implementation(Libraries.AndroidX.APP_COMPAT)
    implementation(Libraries.AndroidX.MARTERIAL)
    implementation(Libraries.AndroidX.CONSTRAINT_LAYOUT)

    //kotlin
    implementation(Libraries.KTX.CORE)

    //LifeCycle
//    implementation(Libraries.KTX.FRAGMENT)

    //hilt
    implementation(Libraries.Hilt.ANDROID)
    kapt(Libraries.Hilt.COMPILER_KAPT)
//
//    //retrofit
//    implementation(Libraries.Retrofit.RETROFIT)
//    implementation(Libraries.Retrofit.GSON)
//
//    //okhttp
//    implementation(Libraries.OkHttp.OKHTTP)
//    implementation(Libraries.OkHttp.INTERCEPTER)

//    //lottie
//    implementation(Libraries.Lottie.LOTTIE)
//
//    //glide
//    implementation(Libraries.Glide.GLIDE)
//    kapt(Libraries.Glide.COMPILER_KAPT)
//
//    implementation (Libraries.Navigation.SAFE_ARGS_GRADLE_PLUGIN)
//    implementation (Libraries.Navigation.FRAGMENT_KTX)
//    implementation (Libraries.Navigation.UI_KTX)

    testImplementation(Libraries.Test.JUNIT)
    androidTestImplementation(Libraries.AndroidTest.ANDROID_JUNIT)
    androidTestImplementation(Libraries.AndroidTest.ESPRESSO)
}