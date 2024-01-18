package pro.moreira.projectpurr.feature.list.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import pro.moreira.projectpurr.common.ui.assets.components.FavoriteIcon
import pro.moreira.projectpurr.common.ui.assets.components.Image
import pro.moreira.projectpurr.common.ui.assets.dimens
import pro.moreira.projectpurr.data.entities.Breed

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BreedItem(
    goToDetails: (String) -> Unit,
    item: Breed,
    isFavoritesTab: Boolean,
    onFavoriteClicked: (String, Boolean) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimens.cardHeight),
        onClick = { goToDetails(item.id) },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimens.normalPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BreedImage(
                url = item.image?.url ?: "",
                isFavorite = item.isFavorite,
                showLifeSpan = isFavoritesTab && item.isFavorite,
                lifeSpan = item.getMaxAverageLifeSpan(),
            ) { onFavoriteClicked(item.id, !item.isFavorite) }
            Spacer(modifier = Modifier.height(dimens.normalPadding))
            BreedName(item.name)
        }
    }
}

@Composable
private fun BreedImage(
    url: String,
    isFavorite: Boolean,
    showLifeSpan: Boolean,
    lifeSpan: String,
    onFavoriteClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f),
        contentAlignment = Alignment.Center,
    ) {
        Card {
            Image(
                modifier = Modifier.fillMaxSize(),
                url = url,
                contentScale = ContentScale.Crop,
            )
        }
        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .testTag("favorite"),
            onClick = onFavoriteClicked,
        ) {
            FavoriteIcon(
                modifier = Modifier
                    .size(dimens.iconSize)
                    .padding(dimens.smallPadding),
                isFavorite = isFavorite,
            )
        }
        if (showLifeSpan) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Text(
                    modifier = Modifier.padding(dimens.smallPadding),
                    text = "Life span: $lifeSpan",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
fun ErrorMessage(state: LoadState.Error) {
    state.error.localizedMessage?.let {
        Text(
            modifier = Modifier.fillMaxSize(),
            text = it,
        )
    }
}

@Composable
private fun BreedName(breedName: String) {
    val titleLarge = MaterialTheme.typography.titleLarge.copy(
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
    )
    var textStyle by remember { mutableStateOf(titleLarge) }
    var readyToDraw by remember { mutableStateOf(false) }
    Text(
        text = breedName,
        style = textStyle,
        maxLines = 1,
        softWrap = false,
        modifier = Modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {
                textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9)
            } else {
                readyToDraw = true
            }
        }
    )
}

@Preview
@Composable
fun Preview() {
    BreedItem(
        goToDetails = {},
        item = Breed(
            "1",
            "Abyssinian",
            "",
            "",
            "",
            pro.moreira.projectpurr.data.entities.Image(
                "",
                "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
            ),
            "12 - 16",
        ),
        isFavoritesTab = true,
    ) { _, _ -> }
}