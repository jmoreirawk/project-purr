package pro.moreira.projectpurr.common.ui.assets.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import pro.moreira.projectpurr.common.ui.assets.R

@Composable
fun Image(
    modifier: Modifier,
    url: String,
    contentScale: ContentScale = ContentScale.FillWidth,
    onClick: (() -> Unit)? = null,
) {
    CoilImage(
        modifier = modifier.let { if (onClick != null) it.clickable(onClick = onClick) else it },
        imageModel = { url },
        imageOptions = ImageOptions(
            contentScale = contentScale,
            alignment = Alignment.Center,
        ),
        loading = {
            Box(modifier = Modifier.matchParentSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        },
        failure = {
            Column(
                modifier = Modifier.matchParentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                content = { Text(text = stringResource(id = R.string.failed_to_load_image)) },
            )
        },
    )
}