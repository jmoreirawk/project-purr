package pro.moreira.projectpurr.feature.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import pro.moreira.projectpurr.common.ui.assets.R.string
import pro.moreira.projectpurr.common.ui.assets.components.FavoriteIcon
import pro.moreira.projectpurr.common.ui.assets.components.Image
import pro.moreira.projectpurr.common.ui.assets.components.Loading
import pro.moreira.projectpurr.common.ui.assets.components.TopBar
import pro.moreira.projectpurr.common.ui.assets.dimens
import pro.moreira.projectpurr.feature.list.navigation.BottomNavBar
import pro.moreira.projectpurr.feature.list.navigation.BottomNavItems

@Composable
fun ListScreen(
    goToDetails: (String) -> Unit,
    viewModel: ListViewModel = hiltViewModel(),
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val errorState = viewModel.errorState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var showFavorites by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBar(stringResource(id = string.app_name)) },
        bottomBar = {
            BottomNavBar(
                items = listOf(
                    BottomNavItems.Breeds { showFavorites = false },
                    BottomNavItems.Favorites { showFavorites = true },
                ),
                showFavorites = showFavorites,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        state.run {
            when (value) {
                is ListScreenState.Loading -> Loading()
                is ListScreenState.Success -> ListContent(
                    paddingValues = it,
                    items = (value as ListScreenState.Success).getListToShow(showFavorites),
                    goToDetails = goToDetails,
                    isFavoritesTab = showFavorites,
                    onSearch = viewModel::onSearch,
                    onFavoriteClicked = viewModel::onFavoriteClicked,
                )
            }
        }
    }

    // Error handling
    LaunchedEffect(errorState.value) {
        errorState.value?.let { snackbarHostState.showSnackbar(it).also { viewModel.clearError() } }
    }
}

@Composable
private fun ListScreenState.Success.getListToShow(showFavorites: Boolean) =
    if (showFavorites) favouriteList.collectAsLazyPagingItems() else list.collectAsLazyPagingItems()

@Composable
private fun ListContent(
    paddingValues: PaddingValues,
    items: LazyPagingItems<ListScreenModel>,
    goToDetails: (String) -> Unit,
    isFavoritesTab: Boolean,
    onSearch: (String) -> Unit,
    onFavoriteClicked: (String, Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Search(!isFavoritesTab, onSearch)
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(2),
            state = rememberLazyGridState(),
            contentPadding = PaddingValues(dimens.largePadding),
            horizontalArrangement = Arrangement.spacedBy(dimens.normalPadding),
            verticalArrangement = Arrangement.spacedBy(dimens.normalPadding),
        ) {
            if (items.itemCount == 0) item {
                Text(text = stringResource(id = string.no_results))
            }
            items(items.itemCount) { index ->
                val item = items[index] ?: return@items
                BreedItem(goToDetails, item, isFavoritesTab, onFavoriteClicked)
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
}

@Composable
private fun ColumnScope.Search(
    showSearch: Boolean,
    onSearch: (String) -> Unit,
) {
    AnimatedVisibility(visible = showSearch) {
        var search by rememberSaveable { mutableStateOf("") }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimens.largePadding),
            label = { Text(text = stringResource(id = string.search)) },
            value = search,
            onValueChange = {
                search = it
                onSearch(search)
            },
            trailingIcon = {
                IconButton(onClick = { search = ""; onSearch(search) }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(id = string.clear),
                    )
                }
            },
            singleLine = true,
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BreedItem(
    goToDetails: (String) -> Unit,
    item: ListScreenModel,
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
                url = item.url,
                isFavorite = item.isFavorite,
                showLifeSpan = isFavoritesTab && item.isFavorite,
                lifeSpan = item.lifeSpan,
            ) { onFavoriteClicked(item.id, !item.isFavorite) }
            Spacer(modifier = Modifier.height(dimens.normalPadding))
            BreedName(item.breedName)
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
        IconButton(modifier = Modifier.align(Alignment.TopEnd), onClick = onFavoriteClicked) {
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
            true,
            "20 years"
        ),
        isFavoritesTab = true,
    ) { _, _ -> }
}