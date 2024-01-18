package pro.moreira.projectpurr.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pro.moreira.projectpurr.data.entities.Breed
import pro.moreira.projectpurr.data.remote.CatRepository
import javax.inject.Inject

@HiltViewModel
class ListViewModel
@Inject constructor(
    private val repository: CatRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListScreenState>(ListScreenState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState = _errorState.asStateFlow()

    init {
        populateUIState(null)
    }

    fun onSearch(query: String) {
        populateUIState(query)
    }

    fun onFavoriteClicked(id: String, isFavorite: Boolean) = viewModelScope.launch {
        runCatching { repository.toggleFavorite(id, isFavorite) }
            .onFailure { error ->
                error.message?.let { _errorState.value = it }
            }
    }

    fun clearError() {
        _errorState.value = null
    }

    private fun populateUIState(query: String?) = viewModelScope.launch {
        _uiState.update {
            ListScreenState.Success(
                getBreeds(query ?: ""),
                getFavorites(),
            )
        }
    }

    private fun getBreeds(query: String) = repository.getCatList(query).map { pagingData ->
        pagingData.map { it.mapIntoListScreenModel() }
    }.cachedIn(viewModelScope)

    private fun getFavorites() = repository.getFavorites().map { pagingData ->
        pagingData.map { it.mapIntoListScreenModel() }
    }.cachedIn(viewModelScope)

    private fun Breed.mapIntoListScreenModel() = ListScreenModel(
        id = id,
        breedName = name,
        url = image?.url ?: "",
        isFavorite = isFavorite,
        lifeSpan = getMaxAverageLifeSpan(),
    )
}