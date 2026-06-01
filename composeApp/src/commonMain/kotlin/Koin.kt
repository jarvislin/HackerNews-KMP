import modules.dataModule
import modules.platformModule
import modules.repositoryModule
import modules.useCaseModule
import modules.viewModelModule
import org.koin.core.context.startKoin
import org.koin.core.error.KoinApplicationAlreadyStartedException

fun initKoin() {
    try {
        startKoin {
            modules(
                dataModule,
                repositoryModule,
                useCaseModule,
                viewModelModule,
                platformModule(),
            )
        }
    } catch (_: KoinApplicationAlreadyStartedException) {
        // Koin is already running in this process.
    }
}
