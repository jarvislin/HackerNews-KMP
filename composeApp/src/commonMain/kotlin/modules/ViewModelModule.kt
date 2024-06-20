package modules

import org.koin.dsl.module
import presentation.viewmodels.MainViewModel

val viewModelModule = module {
    single { MainViewModel(get(), get()) }
}