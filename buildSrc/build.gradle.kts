
repositories {
    mavenCentral()
    google()
}

plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
    implementation("com.google.dagger:hilt-android-gradle-plugin:2.48")
    implementation("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.5")
}