import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.serialization)
}

kotlin {
    androidLibrary {
        namespace = "com.jarvislin.hackernews.core"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
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
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.backhandler)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.koin.compose)
            implementation(libs.napier)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.compose.webview.multiplatform)
            implementation(libs.htmlconverter)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.squircle.shape)
            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.preferences)
        }
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
