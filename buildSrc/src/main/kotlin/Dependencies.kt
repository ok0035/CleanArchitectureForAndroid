object Versions {
    /*
    buildscript {

    ext {
        hilt_ver = "2.42"
        kotlin_ver = "1.8.0"
        fragment_ktx_ver = "1.4.1"
        retrofit_ver = "2.9.0"
        lottie_ver = "3.4.0"
        gson_ver = "2.9.0"
        glide_ver = "4.13.2"
        okhttp_ver = "4.10.0"
    }

    dependencies {
        classpath "com.google.dagger:hilt-android-gradle-plugin:$hilt_ver"
    }
}
     */

    const val APP_COMPAT = "1.4.2"
    const val COROUTINE = "1.3.9"
    const val MARTERIAL = "1.6.1"
    const val CONSTRAINT_LAYOUT = "2.1.4"
    const val HILT_VER = "2.42"
    const val CORE_KTX_VER = "1.8.0"
    const val FRAGMENT_KTX_VER = "1.4.1"
    const val RETROFIT_VER = "2.9.0"
    const val LOTTIE_VER = "3.4.0"
    const val GSON_VER = "2.9.0"
    const val GLIDE_VER = "4.13.2"
    const val OK_HTTP_VER = "4.10.0"
    const val NAV_VER = "2.4.2"
    const val JUNIT_VER = "4.13.2"
    const val AND_JUNIT_VER = "1.1.3"
    const val ESPRESSO = "3.4.0"

}

object Libraries {

    object AndroidX {
        const val APP_COMPAT = "androidx.appcompat:appcompat:${Versions.APP_COMPAT}"
        const val MARTERIAL = "com.google.android.material:material:${Versions.MARTERIAL}"
        const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT}"
    }

    object KTX {
        const val CORE = "androidx.core:core-ktx:${Versions.CORE_KTX_VER}"
        const val FRAGMENT = "androidx.fragment:fragment-ktx:${Versions.FRAGMENT_KTX_VER}"
    }

    object Coroutine {
        const val COROUTINE = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.COROUTINE}"
    }

    object Navigation {
        const val SAFE_ARGS_GRADLE_PLUGIN = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.NAV_VER}"
        const val FRAGMENT_KTX = "androidx.navigation:navigation-fragment-ktx:${Versions.NAV_VER}"
        const val UI_KTX = "androidx.navigation:navigation-ui-ktx:${Versions.NAV_VER}"
    }

    object Hilt {
        const val ANDROID = "com.google.dagger:hilt-android:${Versions.HILT_VER}"
        const val COMPILER_KAPT = "com.google.dagger:hilt-compiler:${Versions.HILT_VER}"
    }

    object Lottie {
        const val LOTTIE = "com.airbnb.android:lottie:${Versions.LOTTIE_VER}"
    }

    object Retrofit {
        const val RETROFIT = "com.squareup.retrofit2:retrofit:${Versions.RETROFIT_VER}"
        const val GSON = "com.squareup.retrofit2:converter-gson:${Versions.GSON_VER}"
    }

    object OkHttp {
        const val OKHTTP = "com.squareup.okhttp3:okhttp:${Versions.OK_HTTP_VER}"
        const val INTERCEPTER = "com.squareup.okhttp3:logging-interceptor:${Versions.OK_HTTP_VER}"
    }

    object Glide {
        const val GLIDE = "com.github.bumptech.glide:glide:${Versions.GLIDE_VER}"
        const val COMPILER_KAPT = "com.github.bumptech.glide:compiler:${Versions.GLIDE_VER}"
    }

    object Test {
        const val JUNIT = "junit:junit:${Versions.JUNIT_VER}"
    }

    object AndroidTest {
         const val ANDROID_JUNIT = "androidx.test.ext:junit:${Versions.AND_JUNIT_VER}" //androidTestImplementation
         const val ESPRESSO = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO}" //androidTestImplementation
    }

}