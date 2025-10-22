import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.api.AndroidBasePlugin
import org.lsposed.lsplugin.ApksignExtension
import org.lsposed.lsplugin.ApksignPlugin

buildscript {
    repositories {
        mavenCentral()
        google()
    }
}
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.lsplugin.jgit)
    alias(libs.plugins.lsplugin.apksign) apply false
    alias(libs.plugins.kotlinx.parcelize) apply false
}
val repo = jgit.repo()
val commitCount = (repo?.commitCount("refs/remotes/origin/main") ?: 1)
val latestTag = repo?.latestTag?.removePrefix("v") ?: "0.0"

val appNamespace by extra("io.github.bkmioa.nexusrss")
val appApplicationId by extra("io.github.bkmioa.nexusrss")

val appVersionCode by extra(commitCount)
val appVersionName by extra(latestTag)

val androidTargetSdkVersion by extra(35)
val androidMinSdkVersion by extra(27)
val androidBuildToolsVersion by extra("35.0.0")
val androidCompileSdkVersion by extra(36)
val androidSourceCompatibility by extra(JavaVersion.VERSION_1_8)
val androidTargetCompatibility by extra(JavaVersion.VERSION_1_8)

tasks.register("Delete", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

subprojects {
    plugins.withType(AndroidBasePlugin::class.java) {
        extensions.configure(CommonExtension::class.java) {
            compileSdk = androidCompileSdkVersion
            buildToolsVersion = androidBuildToolsVersion

            defaultConfig {
                minSdk = androidMinSdkVersion
                if (this is ApplicationDefaultConfig) {
                    targetSdk = androidTargetSdkVersion
                    versionCode = appVersionCode
                    versionName = appVersionName

                    namespace = appNamespace
                    applicationId = appApplicationId
                }
            }
            lint {
                abortOnError = true
                checkReleaseBuilds = false
            }

            compileOptions {
                sourceCompatibility = androidSourceCompatibility
                targetCompatibility = androidTargetCompatibility
            }
        }
    }

    plugins.withType(JavaPlugin::class.java) {
        extensions.configure(JavaPluginExtension::class.java) {
            sourceCompatibility = androidSourceCompatibility
            targetCompatibility = androidTargetCompatibility
        }
    }

    plugins.withType(ApksignPlugin::class.java) {
        extensions.configure(ApksignExtension::class.java) {
            storeFileProperty = "androidStoreFile"
            storePasswordProperty = "androidStorePassword"
            keyAliasProperty = "androidKeyAlias"
            keyPasswordProperty = "androidKeyPassword"
        }
    }
}