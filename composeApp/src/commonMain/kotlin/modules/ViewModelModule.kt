package modules

import org.koin.dsl.module
import presentation.viewmodels.DetailsViewModel
import presentation.viewmodels.MainViewModel

val viewModelModule = module {
    factory { MainViewModel(get(), get()) }
    factory { DetailsViewModel(get()) }
}