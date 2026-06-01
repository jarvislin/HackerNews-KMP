package modules

import Platform
import android.content.Context
import AndroidPlatform
import org.koin.dsl.module

/**
 * Android platform-specific Koin module.
 * Provides Context and platform implementations.
 */
val androidModule = module {
    single<Platform> {
        AndroidPlatform(
            context = get<Context>(),
            versionName = get<AndroidVersionInfo>().versionName,  // From libs.versions.toml: app-version-name
            versionCode = get<AndroidVersionInfo>().versionCode          // From libs.versions.toml: app-version-code
        )
    }
}


class AndroidVersionInfo(
    val versionName: String,
    val versionCode: Int
)
