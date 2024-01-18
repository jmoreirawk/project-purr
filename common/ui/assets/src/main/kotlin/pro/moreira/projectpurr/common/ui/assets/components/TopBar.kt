package pro.moreira.projectpurr.common.ui.assets.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import pro.moreira.projectpurr.common.ui.assets.R
import pro.moreira.projectpurr.common.ui.assets.dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    navigationIcon: @Composable () -> Unit = {},
) = Surface(shadowElevation = dimens.elevation) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            )
        },
        navigationIcon = navigationIcon,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TopBarWithBack(
    title: String,
    onBackClicked: () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    TopBar(
        title = title,
        navigationIcon = {
            IconButton(
                onClick = {
                    keyboardController?.hide()
                    onBackClicked()
                },
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                )
            }
        },
    )
}