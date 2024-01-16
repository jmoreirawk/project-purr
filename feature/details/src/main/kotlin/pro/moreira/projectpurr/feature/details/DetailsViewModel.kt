package pro.moreira.projectpurr.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pro.moreira.projectpurr.data.remote.CatRepository
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel
@Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: CatRepository,
) : ViewModel() {

    private val breedId = savedStateHandle.get<String>("id").orEmpty()

    private val _uiState = MutableStateFlow<DetailsScreenState>(DetailsScreenState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        populateUIState()
    }

    fun onFavoriteClicked(isFavorite: Boolean) = viewModelScope.launch {
        _uiState.value = DetailsScreenState.Loading
        runCatching { repository.toggleFavorite(breedId, isFavorite) }.onSuccess { breed ->
            _uiState.value = DetailsScreenState.Success(breed)
        }.onFailure {
            _uiState.value = DetailsScreenState.Error(it.message)
        }
    }

    fun onRetry() {
        populateUIState(true)
    }

    private fun populateUIState(isRefresh: Boolean = false) = viewModelScope.launch {
        runCatching { repository.getBreed(breedId, isRefresh) }.onSuccess { breed ->
            _uiState.value = DetailsScreenState.Success(breed)
        }.onFailure {
            _uiState.value = DetailsScreenState.Error(it.message)
        }
    }
}