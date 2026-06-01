package modules

import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    // Platform is registered by initKoinAndroid() which runs after initKoin()
}