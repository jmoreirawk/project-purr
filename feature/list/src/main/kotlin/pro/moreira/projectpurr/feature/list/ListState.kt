package pro.moreira.projectpurr.feature.list

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

sealed interface ListScreenState {
    data object Loading : ListScreenState
    data class Success(val list: Flow<PagingData<ListScreenModel>>) : ListScreenState
    data class Error(val message: String) : ListScreenState
}