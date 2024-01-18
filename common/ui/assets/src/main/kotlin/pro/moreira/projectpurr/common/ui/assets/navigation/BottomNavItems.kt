package pro.moreira.projectpurr.common.ui.assets.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import pro.moreira.projectpurr.common.ui.assets.R

sealed class BottomNavItems(
    @StringRes val titleRes: Int,
    val icon: ImageVector,
    val route: String,
) {
    data object Breeds : BottomNavItems(R.string.breeds, Icons.Default.Home, "home")

    data object Favorites : BottomNavItems(R.string.favorites, Icons.Default.Favorite, "favorites")
}