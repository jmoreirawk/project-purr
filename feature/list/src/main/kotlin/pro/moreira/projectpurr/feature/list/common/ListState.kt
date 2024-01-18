package pro.moreira.projectpurr.feature.list.common

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pro.moreira.projectpurr.data.entities.Breed

sealed interface ListScreenState {
    data object Loading : ListScreenState
    data class Success(val list: Flow<PagingData<Breed>>) : ListScreenState
}