package modules

import domain.interactors.GetBestStories
import domain.interactors.GetItems
import org.koin.dsl.module

val useCaseModule = module {
     factory { GetBestStories(get()) }
     factory { GetItems(get()) }
}