package pro.moreira.projectpurr.feature.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import pro.moreira.projectpurr.common.ui.assets.dimens
import pro.moreira.projectpurr.data.entities.Breed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    goBack: () -> Unit,
    viewModel: DetailsViewModel = hiltViewModel(),
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val loading = stringResource(id = string.loading)
    var breedName by remember { mutableStateOf(loading) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = breedName) },
                navigationIcon = {
                    IconButton(
                        onClick = goBack,
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(string.back),
                        )
                    }
                },
            )
        }
    ) {
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
            .padding(dimens.largePadding)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = details.name, style = MaterialTheme.typography.headlineLarge)
            IconButton(onClick = { onFavoriteClicked(!details.isFavorite) }) {
                FavoriteIcon(
                    modifier = Modifier.size(dimens.largeIconSize),
                    isFavorite = details.isFavorite,
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))

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
        Spacer(modifier = Modifier.weight(1f))
        DescriptionEntries(stringResource(id = string.origin), details.origin)
        DescriptionEntries(stringResource(id = string.temperament), details.lifeSpan)
        DescriptionEntries(stringResource(id = string.life_span), details.temperament)
        DescriptionEntries(stringResource(id = string.description), details.description)
    }
}

@Composable
private fun DescriptionEntries(title: String, value: String) {
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(title)
        }
        append(value)
    }
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = dimens.normalPadding),
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
        Button(onClick = onRetryClicked) {
            Text(text = stringResource(string.retry))
        }
    }
}