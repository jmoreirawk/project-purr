package pro.moreira.projectpurr.feature.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pro.moreira.projectpurr.common.ui.assets.R.string
import pro.moreira.projectpurr.common.ui.assets.components.FavoriteIcon
import pro.moreira.projectpurr.common.ui.assets.components.Image
import pro.moreira.projectpurr.common.ui.assets.components.Loading
import pro.moreira.projectpurr.common.ui.assets.components.TopBarWithBack
import pro.moreira.projectpurr.common.ui.assets.dimens
import pro.moreira.projectpurr.data.entities.Breed

@Composable
fun DetailsScreen(
    goBack: () -> Unit,
    viewModel: DetailsViewModel = hiltViewModel(),
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val loading = stringResource(id = string.loading)
    var breedName by remember { mutableStateOf(loading) }

    Scaffold(topBar = { TopBarWithBack(title = breedName, onBackClicked = goBack) }) {
        state.run {
            when (value) {
                is DetailsScreenState.Loading -> Loading()
                is DetailsScreenState.Success -> {
                    DetailsContent(
                        paddingValues = it,
                        details = (value as DetailsScreenState.Success).details,
                        onFavoriteClicked = viewModel::onFavoriteClicked,
                    )
                    breedName = (value as DetailsScreenState.Success).details.name
                }

                is DetailsScreenState.Error -> Error(
                    message = (value as DetailsScreenState.Error).message,
                    onRetryClicked = viewModel::onRetry,
                )
            }
        }
    }
}

@Composable
private fun DetailsContent(
    paddingValues: PaddingValues,
    details: Breed,
    onFavoriteClicked: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(top = dimens.largePadding)
            .padding(horizontal = dimens.largePadding)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        details.image?.url?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = (0.5f * LocalConfiguration.current.screenHeightDp).dp),
                    url = it,
                    contentScale = ContentScale.Crop,
                )
            }
        } ?: Text(text = stringResource(string.no_image))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimens.largePadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DescriptionEntries(
                modifier = Modifier.padding(top = dimens.smallPadding),
                title = stringResource(id = string.origin),
                value = details.origin,
            )
            IconButton(onClick = { onFavoriteClicked(!details.isFavorite) }) {
                FavoriteIcon(
                    modifier = Modifier.size(dimens.largeIconSize),
                    isFavorite = details.isFavorite,
                )
            }
        }
        DescriptionEntries(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(id = string.temperament),
            value = details.temperament,
        )
        DescriptionEntries(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(id = string.description),
            value = details.description,
        )
        DescriptionEntries(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(id = string.life_span),
            value = "${details.lifeSpan} years",
        )
    }
}

@Composable
private fun DescriptionEntries(modifier: Modifier = Modifier, title: String, value: String) {
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(title)
        }
        append(value)
    }
    Text(
        modifier = modifier.then(Modifier.padding(bottom = dimens.largePadding)),
        text = annotatedString,
        style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Left)
    )
}

@Composable
private fun Error(
    message: String?,
    onRetryClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = message.orEmpty())
        OutlinedButton(onClick = onRetryClicked) {
            Text(text = stringResource(string.retry))
        }
    }
}