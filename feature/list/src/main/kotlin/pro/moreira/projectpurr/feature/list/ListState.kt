package pro.moreira.projectpurr.feature.list

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

sealed interface ListScreenState {
    data object Loading : ListScreenState
    data class Success(
        val list: Flow<PagingData<ListScreenModel>>,
        val favouriteList: Flow<PagingData<ListScreenModel>>,
    ) : ListScreenState
}