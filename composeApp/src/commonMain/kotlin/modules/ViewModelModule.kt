package modules

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import presentation.viewmodels.DetailsViewModel
import presentation.viewmodels.MainViewModel

val viewModelModule = module {
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { DetailsViewModel(get(), get()) }
}