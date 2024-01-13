package pro.moreira.projectpurr.data.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pro.moreira.projectpurr.data.models.Breed
import pro.moreira.projectpurr.data.paging.BreedPagingSource
import javax.inject.Inject

class CatRepository
@Inject constructor(private val api: CatApi) {
    fun getCatList(): Flow<PagingData<Breed>> = Pager(
        pagingSourceFactory = { BreedPagingSource(api) },
        config = PagingConfig(pageSize = BreedPagingSource.PAGE_SIZE),
    ).flow

    suspend fun filterBreedsByName(name: String) = api.searchBreed(name)

    suspend fun getBreed(id: String) = api.getBreed(id)
}
