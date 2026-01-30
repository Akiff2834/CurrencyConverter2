plugins {
    alias(libs.plugins.android.application)
    id("androidx.navigation.safeargs")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.currencyconverter"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.currencyconverter"
        minSdk = 24
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

    buildFeatures {
        viewBinding = true
    }

    // 🔽 Testlerde -Xlint:deprecation gibi uyarıları göstermek istersen ekle:
    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.add("-Xlint:deprecation")
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // ✅ Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // ✅ MVVM - ViewModel & LiveData (2nd Semester)
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime:2.7.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.7.0")

    // ✅ Room Database (2nd Semester)
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // ✅ Navigation Component (2nd Semester)
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")

    // ✅ Material Design 3 (2nd Semester)
    implementation("com.google.android.material:material:1.11.0")

    // ✅ Fragment
    implementation("androidx.fragment:fragment:1.6.2")
    
    // ✅ SwipeRefreshLayout (2nd Semester - UI Enhancement)
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // ✅ Hilt Dependency Injection (2nd Semester - Week 6)
    implementation("com.google.dagger:hilt-android:2.48")
    annotationProcessor("com.google.dagger:hilt-compiler:2.48")

    // ✅ WorkManager (2nd Semester - Week 7)
    implementation("androidx.work:work-runtime:2.9.0")
    implementation("androidx.hilt:hilt-work:1.1.0")
    annotationProcessor("androidx.hilt:hilt-compiler:1.1.0")

    // ✅ Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // ✅ Mockito (unit test için)
    testImplementation("org.mockito:mockito-core:5.2.0")

    // ✅ Android özel Mockito (instrumentation test için)
    androidTestImplementation("org.mockito:mockito-android:5.2.0")

    // ✅ MockWebServer (isteğe bağlı ama tavsiye edilir)
    testImplementation("com.squareup.okhttp3:mockwebserver:4.10.0")
}
