package pro.moreira.projectpurr.feature.list.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import pro.moreira.projectpurr.common.ui.assets.R

sealed class BottomNavItems(
    @StringRes val titleRes: Int,
    val icon: ImageVector,
    val route: BottomNavItemsType,
    open val onClick: () -> Unit,
) {
    data class Breeds(override val onClick: () -> Unit) :
        BottomNavItems(R.string.breeds, Icons.Default.Home, BottomNavItemsType.BREEDS, onClick)

    data class Favorites(override val onClick: () -> Unit) : BottomNavItems(
        R.string.favorite,
        Icons.Default.Favorite,
        BottomNavItemsType.FAVORITES,
        onClick,
    )
}