package modules

import IOSPlatform
import Platform
import androidx.compose.ui.ExperimentalComposeUiApi
import org.koin.core.module.Module
import org.koin.dsl.module

@OptIn(ExperimentalComposeUiApi::class)
actual fun platformModule(): Module = module {
    single<Platform> { IOSPlatform() }
}