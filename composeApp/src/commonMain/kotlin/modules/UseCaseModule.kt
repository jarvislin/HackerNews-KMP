package modules

import domain.interactors.GetItems
import domain.interactors.GetStories
import org.koin.dsl.module

val useCaseModule = module {
     factory { GetStories(get()) }
     factory { GetItems(get()) }
}