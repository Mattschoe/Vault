@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

    plugins {
        alias(libs.plugins.kotlinMultiplatform)
        alias(libs.plugins.androidApplication)
        alias(libs.plugins.composeMultiplatform)
        alias(libs.plugins.composeCompiler)
        alias(libs.plugins.composeHotReload)
        alias(libs.plugins.ksp)
        alias(libs.plugins.room)
        id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21"
    }

    kotlin {
        androidTarget {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_11)
            }
        }

        iosX64()
        iosArm64()
        iosSimulatorArm64()
        jvm()

        sourceSets {
            androidMain.dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
            }
            commonMain.dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)

                //Architecture + Navigation
                implementation(libs.navigation.compose)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.json)

                // Room/DataStore
                implementation(libs.androidx.room.runtime)
                implementation(libs.androidx.sqlite.bundled)
                api(libs.datastore.preferences)
                api(libs.datastore)
            }
            commonTest.dependencies {
                implementation(libs.kotlin.test)
            }
            jvmMain.dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutinesSwing)
            }
        }
    }

    android {
        namespace = "org.creategoodthings.vault"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        defaultConfig {
            applicationId = "org.creategoodthings.vault"
            minSdk = libs.versions.android.minSdk.get().toInt()
            targetSdk = libs.versions.android.targetSdk.get().toInt()
            versionCode = 1
            versionName = "1.0"
        }
        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
        buildTypes {
            getByName("release") {
                isMinifyEnabled = false
            }
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    dependencies {
        debugImplementation(compose.uiTooling)

        ksp(libs.androidx.room.compiler)

        //Room
        add("kspIosX64", libs.androidx.room.compiler)
        add("kspIosArm64", libs.androidx.room.compiler)
        add("kspIosSimulatorArm64", libs.androidx.room.compiler)
        add("kspJvm", libs.androidx.room.compiler)
    }

    compose.desktop {
        application {
            mainClass = "org.creategoodthings.vault.MainKt"

            nativeDistributions {
                targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
                packageName = "org.creategoodthings.vault"
                packageVersion = "1.0.0"
            }
        }
    }


