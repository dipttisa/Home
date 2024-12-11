plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
}

android {
    namespace = "com.example.home"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.home"
        minSdk = 21
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation ("com.android.support:appcompat-v7:28.0.0")
    implementation ("com.android.support:design:28.0.0")
    implementation ("com.android.support.constraint:constraint-layout:1.1.3")
    implementation ("com.google.android.material:material:1.4.0")



    implementation("org.mindrot:jbcrypt:0.4")


    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:realtime-kt")


    implementation(platform("io.github.jan-tennert.supabase:bom:2.2.3"))

    implementation("io.github.jan-tennert.supabase:gotrue-kt")

    implementation ("io.ktor:ktor-client-cio:2.3.0")


    implementation ("com.squareup.picasso:picasso:2.8")

    implementation ("androidx.fragment:fragment-ktx:1.6.0")

    implementation ("com.squareup.okhttp3:okhttp:4.10.0")
            implementation ("io.github.jan-tennert.supabase:supabase-kt:1.0.0")





    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}