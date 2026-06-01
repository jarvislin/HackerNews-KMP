package com.jarvislin.hackernews

import android.app.Application
import android.content.Context
import modules.AndroidVersionInfo
import modules.androidModule
import org.koin.core.context.GlobalContext
import org.koin.dsl.module

/**
 * Initialize Android-specific Koin modules after the common initialization.
 * This should be called from Application.onCreate after initKoin().
 */
fun Application.initKoinAndroid() {
    // Register Android Context to the already-running Koin container
    val contextModule = module {
        single<Context> { this@initKoinAndroid }
        single<AndroidVersionInfo> {
            AndroidVersionInfo(
                versionName = BuildConfig.VERSION_NAME,
                versionCode = BuildConfig.VERSION_CODE
            )
        }
    }
    GlobalContext.get().loadModules(listOf(contextModule, androidModule))
}

