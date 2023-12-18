plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.watdagam.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.watdagam.android"
        minSdk = 26
        targetSdk = 33
        versionCode = 3
        versionName = "0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        versionNameSuffix = "alpha-test"
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

    // 하단 네비게이션 바에 뷰 바인딩 적용
    viewBinding {
        enable = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // 카카오 API
    implementation("com.kakao.sdk:v2-all-rx:2.18.0") // 전체 모듈 설치, 2.11.0 버전부터 지원
    implementation("com.kakao.sdk:v2-user-rx:2.18.0") // 카카오 로그인

    // retrofit HTTP 통신
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.4.0")

    // 뷰 바인딩
    implementation("androidx.compose.ui:ui-viewbinding:1.5.4")

    // 코루틴
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.activity:activity-ktx:1.8.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // Shared Preference 암호화
    implementation("androidx.security:security-crypto-ktx:1.1.0-alpha06")

    // Location 정보
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Room
    val room_version = "2.6.0"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.room:room-rxjava2:$room_version")
    implementation("androidx.room:room-rxjava3:$room_version")
    implementation("androidx.room:room-guava:$room_version")
    testImplementation("androidx.room:room-testing:$room_version")
    implementation("androidx.room:room-paging:$room_version")

    // 당겨서 새로고침
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Lottie Animation
    implementation("com.airbnb.android:lottie:6.2.0")

    // Ted Permission
    val ted_version = "3.3.0"
    implementation("io.github.ParkSangGwon:tedpermission-normal:$ted_version")
    implementation("io.github.ParkSangGwon:tedpermission-coroutine:$ted_version")

    // ADMob
    implementation("com.google.android.gms:play-services-ads:22.5.0")
}