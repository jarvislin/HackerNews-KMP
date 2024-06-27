import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import modules.dataModule
import modules.repositoryModule
import modules.useCaseModule
import modules.viewModelModule
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import presentation.screens.MainScreen
import ui.appTypography
import ui.darkScheme
import ui.lightScheme

@Composable
@Preview
fun App() {
    KoinApplication(moduleList = {
        listOf(
            dataModule,
            repositoryModule,
            useCaseModule,
            viewModelModule
        )
    }) {
        val colors = if (isSystemInDarkTheme()) {
            darkScheme
        } else {
            lightScheme
        }

        MaterialTheme(
            typography = appTypography(),
            colorScheme = colors
        ) {
            Navigator(MainScreen()) {
                SlideTransition(it)
            }
        }
    }
}
