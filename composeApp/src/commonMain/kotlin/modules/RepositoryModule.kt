package modules

import domain.repositories.ItemRepository
import org.koin.dsl.module
import presentation.repositories.ItemRepositoryImpl

val repositoryModule = module {
    factory<ItemRepository> { ItemRepositoryImpl(get(), get()) }
}