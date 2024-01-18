package pro.moreira.projectpurr

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pro.moreira.projectpurr.feature.list.ListScreen

@Composable
fun MainNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            ListScreen(goToDetails = { id ->
                navController.navigate("details/$id")
            })
        }

        composable(
            "details/{id}",
            listOf(navArgument("id") { type = NavType.StringType })
        ) {
            // TODO
        }
    }
}