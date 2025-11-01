plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.lsplugin.apksign)
    alias(libs.plugins.kotlinx.parcelize)
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.activity.compose)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.material.icons.extended)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)


    //lifecycle
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.viewmodel.savedstate)

    //room
    implementation(libs.room.ktx)
    implementation(libs.room.rxjava2)
    ksp(libs.room.compiler)

    implementation(libs.koin.android)

    //rx
    implementation(libs.rxjava2)
    implementation(libs.rxandroid)

    //okhttp
    implementation(libs.okhttp)
    implementation(libs.okhttp.loggingInterceptor)
    implementation(libs.okhttp.cookiejar)


    //retrofit
    implementation(libs.retrofit2)
    implementation(libs.retrofit2.adapter.rxjava2)
    implementation(libs.retrofit2.converter.gson)

    //kotpref
    implementation(libs.kotpref)
    implementation(libs.kotpref.gsonsupport)

    implementation(libs.apache.commons.text)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.rx2)
    implementation(libs.mavericks)
    implementation(libs.mavericks.compose)
    implementation(libs.paging.compose)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)

    implementation(libs.compose.destinations.core)
    ksp(libs.compose.destinations.ksp)

    implementation(libs.reorderable)

    implementation(libs.versioncompare)
    implementation(libs.haze)
    implementation(libs.haze.materials)
}