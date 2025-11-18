import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
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
            implementation(libs.kotlin.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.koin.compose)
            implementation(libs.napier)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.compose.webview.multiplatform)
            implementation(libs.htmlconverter)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.squircle.shape)
            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.ui.backhandler)
        }
    }
}

android {
    namespace = "com.jarvislin.hackernews"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.jarvislin.hackernews"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = libs.versions.app.version.code.get().toInt()
        versionName = libs.versions.app.version.name.get()
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
    buildFeatures {
        compose = true
        buildConfig = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)
    }
}

/**
 * Convenient hook to run code generation type tasks when project is built.
 * See: https://medium.com/@rrmunro/building-deploying-a-simple-kmp-app-part-6-release-ci-on-github-bfc8bb2783cc
 */
tasks.named("generateComposeResClass") {
    dependsOn("updatePlistVersion")
}

/**
 * Pulls the latest appVersion from libs.versions.toml, and updates
 * the `Info.plist` file in the iosApp project.
 * See: https://medium.com/@rrmunro/building-deploying-a-simple-kmp-app-part-6-release-ci-on-github-bfc8bb2783cc
 */
tasks.register("updatePlistVersion") {
    val plistFile = project.file("../iosApp/iosApp/Info.plist") // Path to `Info.plist` file in iOS app project

    inputs.property("versionName", libs.versions.app.version.name)
    inputs.property("versionCode", libs.versions.app.version.code)
    outputs.file(plistFile)

    doLast {
        if (!plistFile.exists()) {
            throw GradleException("Info.plist not found at ${plistFile.absolutePath}")
        }

        val appVersionName: String = libs.versions.app.version.name.get()
        val appVersionCode: Int = libs.versions.app.version.code.get().toInt()

        var plistContent = plistFile.readText()

        println("Updating iOS app version name in ${plistFile.absoluteFile} to $appVersionName")
        plistContent = plistContent.replace(
            Regex("<key>CFBundleShortVersionString</key>\\s*<string>.*?</string>"),
            "<key>CFBundleShortVersionString</key>\n\t<string>$appVersionName</string>"
        )
        println("Updating iOS app version code in ${plistFile.absoluteFile} to $appVersionCode")
        plistContent = plistContent.replace(
            Regex("<key>CFBundleVersion</key>\\s*<string>.*?</string>"),
            "<key>CFBundleVersion</key>\n\t<string>$appVersionCode</string>"
        )

        plistFile.writeText(plistContent)
    }
}