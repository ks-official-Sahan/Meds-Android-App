plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    //id("kotlin-kapt")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.sahansachintha.meds"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sahansachintha.meds"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0-alpha6"

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
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.work.runtime)
    //implementation("androidx.work:work-runtime:2.10.0")
    implementation(libs.guava)
    //implementation("com.google.guava:guava:32.1.3-android")

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.auth)
    //implementation(platform("com.google.firebase:firebase-bom:33.8.0"))
    //implementation("com.google.firebase:firebase-firestore")
    //implementation("com.google.firebase:firebase-messaging")
    //implementation("com.google.firebase:firebase-auth:23.2.0")

    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    implementation(libs.gson)
    //implementation("com.google.code.gson:gson:2.11.0")

    implementation(libs.okhttp)
    //implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation (libs.retrofit)
    //implementation ("com.squareup.retrofit2:retrofit:2.4.0")

    //implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.glide)
    //kapt("com.github.bumptech.glide:compiler:4.16.0")
    //kapt(libs.compiler)
    //ksp("com.github.bumptech.glide:compiler:4.16.0")
    //ksp(libs.compiler)
    //ksp("com.github.bumptech.glide:ksp:4.14.2")
    ksp(libs.ksp)

    //implementation("com.github.PayHereDevs:payhere-android-sdk:v3.0.17")
    implementation(libs.payhere.android.sdk)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}