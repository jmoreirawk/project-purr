package pro.moreira.projectpurr.common.ui.assets

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimens(
    // Padding
    val smallPadding: Dp = 4.dp,
    val normalPadding: Dp = 8.dp,
    val largePadding: Dp = 16.dp,
    // Size
    val cardHeight: Dp = 200.dp,
    val iconSize: Dp = 40.dp,
    val largeIconSize: Dp = 80.dp,
    // Elevation
    val elevation: Dp = 4.dp,
)

val dimens: Dimens
    @Composable
    get() = Dimens()