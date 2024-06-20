package modules

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val dataModule = module {
    single { Json { ignoreUnknownKeys = true } }
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
}