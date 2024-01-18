package pro.moreira.projectpurr.feature.list.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import pro.moreira.projectpurr.common.ui.assets.R
import pro.moreira.projectpurr.common.ui.assets.components.Loading
import pro.moreira.projectpurr.common.ui.assets.dimens
import pro.moreira.projectpurr.common.ui.assets.navigation.ContainerScaffold
import pro.moreira.projectpurr.data.entities.Breed
import pro.moreira.projectpurr.feature.list.common.BreedItem
import pro.moreira.projectpurr.feature.list.common.ErrorMessage
import pro.moreira.projectpurr.feature.list.common.ListScreenState

@Composable
fun FavoritesScreen(
    navController: NavController,
    goToDetails: (String) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val errorState = viewModel.errorState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ContainerScaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        navController = navController,
    ) {
        state.run {
            when (value) {
                is ListScreenState.Loading -> Loading()
                is ListScreenState.Success -> ListContent(
                    paddingValues = it,
                    items = (value as ListScreenState.Success).list.collectAsLazyPagingItems(),
                    goToDetails = goToDetails,
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
private fun ListContent(
    paddingValues: PaddingValues,
    items: LazyPagingItems<Breed>,
    goToDetails: (String) -> Unit,
    onFavoriteClicked: (String, Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(2),
            state = rememberLazyGridState(),
            contentPadding = PaddingValues(dimens.largePadding),
            horizontalArrangement = Arrangement.spacedBy(dimens.normalPadding),
            verticalArrangement = Arrangement.spacedBy(dimens.normalPadding),
        ) {
            if (items.itemCount == 0) item {
                Text(text = stringResource(id = R.string.no_results))
            }
            items(items.itemCount) { index ->
                val item = items[index] ?: return@items
                BreedItem(goToDetails, item, false, onFavoriteClicked)
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