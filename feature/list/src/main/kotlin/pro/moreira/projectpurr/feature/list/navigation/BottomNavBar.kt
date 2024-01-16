package pro.moreira.projectpurr.feature.list.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import pro.moreira.projectpurr.common.ui.assets.Typography
import pro.moreira.projectpurr.common.ui.assets.dimens

@Composable
fun BottomNavBar(
    items: List<BottomNavItems>,
    showFavorites: Boolean,
) {
    val currentRoute =
        if (showFavorites) BottomNavItemsType.FAVORITES else BottomNavItemsType.BREEDS
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(item.titleRes),
                    )
                },
                label = {
                    Text(
                        modifier = Modifier.padding(top = dimens.smallPadding),
                        text = stringResource(item.titleRes),
                        style = Typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    )
                },
                alwaysShowLabel = false,
                selected = currentRoute == item.route,
                onClick = item.onClick,
            )
        }
    }
}