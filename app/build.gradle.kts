plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.google.hilt)
}

android {
    namespace = "de.karelwhite.draftable"
    compileSdk = 36

    defaultConfig {
        applicationId = "de.karelwhite.draftable"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
    buildToolsVersion = "35.0.0"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.material)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.gson)
    implementation(libs.androidx.room.runtime.android)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.material.icons.extended)
    ksp(libs.androidx.room.compiler)
    ksp(libs.hilt.compiler)
    //testImplementation(libs.junit)
    //androidTestImplementation(libs.androidx.junit)
    //androidTestImplementation(libs.androidx.espresso.core)
    //androidTestImplementation(platform(libs.androidx.compose.bom))
    //androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    testImplementation(platform("org.junit:junit-bom:5.10.5"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")


    // JUnit 5 für lokale Unit-Tests (src/test) - Versionen kommen von der BOM
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.junit.jupiter.params)

    // Mockito für Mocking
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)

    // Für Coroutine-Tests
    testImplementation(libs.kotlinx.coroutines.test)

}