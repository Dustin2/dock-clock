plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

android {
    namespace = "io.github.dustin2.dockclock"
    compileSdk = 37

    defaultConfig {
        applicationId = "io.github.dustin2.dockclock"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "0.1.0"
    }

    signingConfigs {
        create("release") {
            // Read from the environment (CI secrets), so the key never lives in the repository.
            System.getenv("DOCKCLOCK_KEYSTORE_PATH")?.let {
                storeFile = file(it)
                storePassword = System.getenv("DOCKCLOCK_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("DOCKCLOCK_KEY_ALIAS")
                keyPassword = System.getenv("DOCKCLOCK_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            // R8 removes unused code and resources, shrinking the APK.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            // Without the release key (local builds), sign with the debug key so the
            // minified build can still be installed and tested. CI checks the key is present.
            val release = signingConfigs.getByName("release")
            signingConfig =
                if (release.storeFile != null) release else signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        compose = true
    }
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(rootProject.file("config/detekt/detekt.yml"))
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
}
