package presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import presentation.screens.about.AboutRoute
import presentation.screens.about.AboutScreen
import presentation.screens.details.DetailsRoute
import presentation.screens.details.DetailsScreen
import presentation.screens.details.DetailsScreenTab
import presentation.screens.main.MainRoute
import presentation.screens.main.MainScreen

@Composable
fun RootScreen(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = MainRoute,
        modifier = Modifier.fillMaxSize()
    ) {
        composable<MainRoute> {
            MainScreen(
                onClickItem = { navController.navigate(DetailsRoute(id = it.getItemId(), tab = DetailsScreenTab.Webview.name)) },
                onClickComment = { navController.navigate(DetailsRoute(id = it.getItemId(), tab = DetailsScreenTab.Comments.name)) },
                onClickAbout = { navController.navigate(AboutRoute) },
            )
        }

        composable<DetailsRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<DetailsRoute>()
            DetailsScreen(
                itemId = route.id,
                tab = DetailsScreenTab.from(route.tab),
                onBack = { navController.popBackStack() },
            )
        }

        composable<AboutRoute> {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}
