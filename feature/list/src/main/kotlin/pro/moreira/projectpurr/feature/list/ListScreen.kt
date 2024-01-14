package pro.moreira.projectpurr.feature.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import pro.moreira.projectpurr.common.ui.assets.R.drawable
import pro.moreira.projectpurr.common.ui.assets.R.string
import pro.moreira.projectpurr.common.ui.assets.components.Image
import pro.moreira.projectpurr.common.ui.assets.components.Loading
import pro.moreira.projectpurr.common.ui.assets.dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    goToDetails: (String) -> Unit,
    viewModel: ListViewModel = hiltViewModel(),
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            Surface(shadowElevation = dimens.elevation) {
                TopAppBar(title = {
                    Text(
                        text = stringResource(id = string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                    )
                })
            }
        },
        bottomBar = {}, // TODO Add bottom bar
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        state.run {
            when (value) {
                is ListScreenState.Loading -> Loading()
                is ListScreenState.Success -> Content(
                    it,
                    (value as ListScreenState.Success).list.collectAsLazyPagingItems(),
                    goToDetails,
                )

                is ListScreenState.Error -> LaunchedEffect(value) {
                    snackbarHostState.showSnackbar((value as ListScreenState.Error).message)
                }
            }
        }
    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    items: LazyPagingItems<ListScreenModel>,
    goToDetails: (String) -> Unit,
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(dimens.normalPadding),
        horizontalArrangement = Arrangement.spacedBy(dimens.normalPadding),
        verticalArrangement = Arrangement.spacedBy(dimens.normalPadding),
    ) {
        items(items.itemCount) { index ->
            val item = items[index] ?: return@items
            BreedItem(goToDetails, item)
        }
        item {
            when {
                items.loadState.refresh is LoadState.Error -> ErrorMessage(items.loadState.refresh as LoadState.Error)
                items.loadState.append is LoadState.Error -> ErrorMessage(items.loadState.append as LoadState.Error)
                items.loadState.refresh is LoadState.Loading -> Loading()
                items.loadState.append is LoadState.Loading -> Loading()
                else -> {}
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BreedItem(
    goToDetails: (String) -> Unit,
    item: ListScreenModel,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimens.cardHeight),
        onClick = { goToDetails(item.id) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimens.normalPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BreedImage(item.url, item.isFavorite)
            Spacer(modifier = Modifier.height(dimens.normalPadding))
            BreedName(item.breedName)
        }
    }
}

@Composable
private fun BreedImage(url: String, isFavorite: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            url = url,
            contentScale = ContentScale.Crop,
        )
        val painter =
            painterResource(id = if (isFavorite) drawable.ic_fav else drawable.ic_not_fav)
        Icon(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(dimens.iconSize)
                .padding(dimens.smallPadding),
            painter = painter,
            contentDescription = stringResource(id = string.favorite),
            tint = Color.Red,
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

@Composable
private fun ErrorMessage(state: LoadState.Error) {
    state.error.localizedMessage?.let {
        Text(
            modifier = Modifier.fillMaxSize(),
            text = it,
        )
    }
}

@Preview
@Composable
fun Preview() {
    BreedItem(
        goToDetails = {},
        item = ListScreenModel(
            "1",
            "Abyssinian",
            "https://cdn2.thecatapi.com/images/0XYvRd7oD.jpg",
            false,
        ),
    )
}