package pro.moreira.projectpurr.feature.list.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pro.moreira.projectpurr.data.remote.CatRepository
import pro.moreira.projectpurr.feature.list.common.ListScreenState
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
            .onFailure { error -> error.message?.let { _errorState.value = it } }
    }

    fun clearError() {
        _errorState.value = null
    }

    private fun populateUIState(query: String?) = viewModelScope.launch {
        _uiState.update {
            ListScreenState.Success(repository.getCatList(query ?: "").cachedIn(viewModelScope))
        }
    }
}