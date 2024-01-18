package pro.moreira.projectpurr

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pro.moreira.projectpurr.feature.details.DetailsScreen
import pro.moreira.projectpurr.feature.list.favorites.FavoritesScreen
import pro.moreira.projectpurr.feature.list.list.ListScreen

@Composable
fun MainNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            ListScreen(navController, { id -> navController.navigate("details/$id") })
        }

        composable("favorites") {
            FavoritesScreen(navController, { id -> navController.navigate("details/$id") })
        }

        composable(
            "details/{id}",
            listOf(navArgument("id") { type = NavType.StringType })
        ) {
            DetailsScreen(
                goBack = { navController.popBackStack() },
            )
        }
    }
}