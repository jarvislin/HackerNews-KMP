package modules

import domain.interactors.GetComments
import domain.interactors.GetItems
import domain.interactors.GetPollOptions
import domain.interactors.GetStories
import org.koin.dsl.module

val useCaseModule = module {
     factory { GetStories(get()) }
     factory { GetItems(get()) }
     factory { GetComments(get()) }
     factory { GetPollOptions(get()) }
}