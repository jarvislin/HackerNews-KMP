package presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import presentation.screens.details.DetailsRoute
import presentation.screens.details.DetailsScreen
import presentation.screens.main.MainScreen

enum class RouteScreen {
    Main,
    Details,
}

@Composable
fun RootScreen(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = RouteScreen.Main.name,
        modifier = Modifier.fillMaxSize().statusBarsPadding()
    ) {
        composable(RouteScreen.Main.name) {
            MainScreen(
                onClickItem = { navController.navigate(DetailsRoute(it.getItemId())) },
                onClickComment = { navController.navigate(DetailsRoute(it.getItemId())) }
            )
        }

        composable<DetailsRoute> { backStackEntry ->
            DetailsScreen(
                itemId = backStackEntry.toRoute<DetailsRoute>().id,
                onBack = { navController.popBackStack() },
            )
        }
    }
}