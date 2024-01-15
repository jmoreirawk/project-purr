package pro.moreira.projectpurr.common.ui.assets.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import pro.moreira.projectpurr.common.ui.assets.R

@Composable
fun FavoriteIcon(
    modifier: Modifier,
    isFavorite: Boolean,
) {
    val painter = painterResource(
        id = if (isFavorite) R.drawable.ic_fav else R.drawable.ic_not_fav
    )
    Icon(
        modifier = modifier,
        painter = painter,
        contentDescription = stringResource(id = R.string.favorite),
        tint = Color.Red,
    )
}