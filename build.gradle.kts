plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.mebubilisim.trtoolspro"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mebubilisim.trtoolspro"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
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
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.games.activity)
<<<<<<< HEAD
=======
    implementation (libs.androidx.camera.core)
    implementation (libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.view)
    implementation (libs.androidx.camera.extensions)
>>>>>>> bafdf16 (2 commit)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.animated.navigation.bar)
    implementation (libs.material3)
    //navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.msz.progress.indicator)
    implementation (libs.androidx.runtime.livedata)
    implementation (libs.play.services.location)
    implementation (libs.play.services.maps)
    implementation (libs.kotlinx.coroutines.play.services)
    implementation (libs.play.services.maps.v1802)
    implementation (libs.maps.compose)
    implementation (libs.kotlinx.coroutines.play.services.v173)

    implementation (libs.ui)
    implementation (libs.ui.tooling)
    implementation (libs.androidx.foundation)
    implementation (libs.androidx.material)
    implementation (libs.androidx.activity.compose.v151)
    implementation (libs.androidx.preference.ktx)
    implementation (libs.androidx.datastore.preferences)
<<<<<<< HEAD

=======
    implementation (libs.androidx.animation)
>>>>>>> bafdf16 (2 commit)

}
