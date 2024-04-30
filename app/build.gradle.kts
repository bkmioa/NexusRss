plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.lsplugin.apksign)
    alias(libs.plugins.kotlinx.parcelize)
    id("com.jakewharton.butterknife")
}

android {
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity.compose)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.material.icons.extended)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //support
    implementation(libs.recyclerview)
    implementation(libs.swiperefreshlayout)
    implementation(libs.fragment.ktx)


    //lifecycle
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.viewmodel.savedstate)
    ksp(libs.lifecycle.common.java8)

    //room
    implementation(libs.room.ktx)
    implementation(libs.room.rxjava2)
    ksp(libs.room.compiler)

    implementation(libs.constraintlayout)
    implementation(libs.koin.android)

    //rx
    implementation(libs.rxjava2)
    implementation(libs.rxandroid)

    //okhttp
    implementation(libs.okhttp)
    implementation(libs.okhttp.loggingInterceptor)

    //retrofit
    implementation(libs.retrofit2)
    implementation(libs.retrofit2.adapter.rxjava2)
    implementation(libs.retrofit2.converter.gson)

    //glide
    implementation(libs.glide)
    ksp(libs.glide.ksp)
    implementation(libs.glide.okhttp3.integration)

    //epoxy
    implementation(libs.epoxy)
    ksp(libs.epoxy.processor)

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
}
configurations.all {
    //resolutionStrategy.force("androidx.compose.material3:material3:1.1.2")
}