package pro.moreira.projectpurr.common.ui.assets.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import pro.moreira.projectpurr.common.ui.assets.R
import pro.moreira.projectpurr.common.ui.assets.components.TopBar

@Composable
fun ContainerScaffold(
    snackbarHost: @Composable () -> Unit,
    navController: NavController,
    screen: @Composable (PaddingValues) -> Unit,
) {
    val bottomNavType = listOf(
        BottomNavItems.Breeds,
        BottomNavItems.Favorites,
    )

    Scaffold(
        topBar = { TopBar(stringResource(id = R.string.app_name)) },
        bottomBar = { BottomNavBar(navController, bottomNavType) },
        content = { screen(it) },
        snackbarHost = snackbarHost,
    )
}