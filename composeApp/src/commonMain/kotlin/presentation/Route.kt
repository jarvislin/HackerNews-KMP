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
import domain.models.getUrl
import presentation.screens.DetailsRoute
import presentation.screens.DetailsScreen
import presentation.screens.MainScreen
import presentation.screens.WebRoute
import presentation.screens.WebScreen

enum class RouteScreen {
    Main,
    Details,
    Web
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
                onClickItem = {
                    if (it.getUrl() != null) {
                        navController.navigate(WebRoute(it.getItemId()))
                    } else {
                        navController.navigate(DetailsRoute(it.getItemId()))
                    }
                },
                onClickComment = { navController.navigate(DetailsRoute(it.getItemId())) }
            )
        }
        composable<WebRoute> { backStackEntry ->
            WebScreen(
                itemId = backStackEntry.toRoute<WebRoute>().id,
                onBack = { navController.popBackStack() },
                onClickItem = { navController.navigate(DetailsRoute(it.getItemId())) }
            )
        }

        composable<DetailsRoute> { backStackEntry ->
            DetailsScreen(
                itemId = backStackEntry.toRoute<DetailsRoute>().id,
                onBack = { navController.popBackStack() },
                onClickItem = { navController.navigate(WebRoute(it.getItemId())) }
            )
        }
    }
}