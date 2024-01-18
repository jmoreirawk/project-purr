package pro.moreira.projectpurr.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import pro.moreira.projectpurr.data.entities.Breed
import pro.moreira.projectpurr.data.local.CatLocalDataSource
import pro.moreira.projectpurr.data.local.dao.RemoteKeyDao
import pro.moreira.projectpurr.data.paging.BreedPagingSource
import javax.inject.Inject

class CatRepository
@Inject constructor(
    private val api: CatApi,
    private val localDataSource: CatLocalDataSource,
    private val remoteKeyDao: RemoteKeyDao,
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getCatList(name: String?): Flow<PagingData<Breed>> = Pager(
        pagingSourceFactory = { localDataSource.getBreeds(name) },
        config = PagingConfig(
            pageSize = BreedPagingSource.PAGE_SIZE,
            prefetchDistance = 5,
            enablePlaceholders = true,
        ),
        remoteMediator = CatsRemoteMediator(name, localDataSource, remoteKeyDao, api),
    ).flow

    suspend fun getBreed(id: String, refresh: Boolean): Breed = withContext(Dispatchers.IO) {
        if (refresh) {
            val breed = api.getBreed(id)
            localDataSource.updateBreed(breed)
        }
        localDataSource.getBreed(id)
    } ?: throw Exception("Breed not found")


    suspend fun toggleFavorite(id: String, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        localDataSource.updateFavorite(id, isFavorite)
        return@withContext getBreed(id, false)
    }

    fun getFavorites() = localDataSource.getFavorites()
}
