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
import pro.moreira.projectpurr.data.remote.CatRepository
import javax.inject.Inject

@HiltViewModel
class ListViewModel
@Inject constructor(
    private val repository: CatRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListScreenState>(ListScreenState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        populateUIState(null)
    }

    fun onSearch(query: String) {
        populateUIState(query)
    }

    fun onFavoriteClicked(id: String, isFavorite: Boolean) = viewModelScope.launch {
        runCatching { repository.toggleFavorite(id, isFavorite) }
            .onFailure { error ->
                error.message?.let { _uiState.value = ListScreenState.Error(it) }
            }
    }

    private fun populateUIState(query: String?) = viewModelScope.launch {
        _uiState.update {
            ListScreenState.Success(
                repository.getCatList(query).map { pagingData ->
                    pagingData.map { breed ->
                        ListScreenModel(
                            id = breed.id,
                            breedName = breed.name,
                            url = breed.image?.url ?: "",
                            isFavorite = breed.isFavorite,
                        )
                    }
                }.cachedIn(viewModelScope)
            )
        }
    }
}