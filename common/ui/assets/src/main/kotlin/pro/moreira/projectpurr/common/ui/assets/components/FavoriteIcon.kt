package pro.moreira.projectpurr.common.ui.assets.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import pro.moreira.projectpurr.common.ui.assets.R

@Composable
fun FavoriteIcon(
    modifier: Modifier,
    isFavorite: Boolean,
) {
    Icon(
        modifier = modifier,
        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
        contentDescription = stringResource(id = R.string.favorite),
        tint = Color.Red,
    )
}