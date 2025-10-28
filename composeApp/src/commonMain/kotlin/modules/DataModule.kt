package modules

import data.remote.ApiHandler
import domain.models.Ask
import domain.models.Comment
import domain.models.Item
import domain.models.Job
import domain.models.Poll
import domain.models.PollOption
import domain.models.Story
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.dsl.module

val dataModule = module {
    single {
        val module = SerializersModule {
            polymorphic(Item::class) {
                subclass(Ask::class, Ask.serializer())
                subclass(Comment::class, Comment.serializer())
                subclass(Job::class, Job.serializer())
                subclass(Poll::class, Poll.serializer())
                subclass(PollOption::class, PollOption.serializer())
                subclass(Story::class, Story.serializer())
            }
        }
        Json {
            serializersModule = module
            ignoreUnknownKeys = true
            classDiscriminator = "kind" // Because "type" is a named field in the HN api
        }
    }
    single {
        HttpClient {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.i(message)
                    }
                }
            }
        }.also { Napier.base(DebugAntilog()) }
    }
    single { ApiHandler }
}