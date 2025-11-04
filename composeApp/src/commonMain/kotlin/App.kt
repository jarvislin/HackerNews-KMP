import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import modules.dataModule
import modules.repositoryModule
import modules.useCaseModule
import modules.viewModelModule
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import presentation.RootScreen
import ui.AppTheme

@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(
            dataModule,
            repositoryModule,
            useCaseModule,
            viewModelModule
        )
    }) {
        AppTheme {
            RootScreen()
        }
    }
}
