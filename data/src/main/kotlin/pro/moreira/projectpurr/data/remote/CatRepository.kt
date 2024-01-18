package pro.moreira.projectpurr.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import pro.moreira.projectpurr.data.entities.Breed
import pro.moreira.projectpurr.data.entities.Favorite
import pro.moreira.projectpurr.data.local.AppDatabase
import pro.moreira.projectpurr.data.local.CatLocalDataSource
import pro.moreira.projectpurr.data.local.dao.FavoriteDao
import pro.moreira.projectpurr.data.local.dao.RemoteKeyDao
import pro.moreira.projectpurr.data.paging.CatsRemoteMediator
import javax.inject.Inject

class CatRepository
@Inject constructor(
    private val api: CatApi,
    private val database: AppDatabase,
    private val localDataSource: CatLocalDataSource,
    private val remoteKeyDao: RemoteKeyDao,
    private val favoriteDao: FavoriteDao,
) {
    companion object {
        const val PAGE_SIZE = 20
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getCatList(query: String): Flow<PagingData<Breed>> = Pager(
        pagingSourceFactory = { localDataSource.getBreeds(query) },
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            prefetchDistance = 5,
            enablePlaceholders = true,
        ),
        remoteMediator = CatsRemoteMediator(
            query,
            database,
            localDataSource,
            remoteKeyDao,
            favoriteDao,
            api,
        ),
    ).flow

    suspend fun getBreed(id: String, refresh: Boolean): Breed = withContext(Dispatchers.IO) {
        val localBreed = localDataSource.getBreed(id)
        if (refresh) getAndUpdateBreedFromRemote(id, localBreed)
        return@withContext localBreed
    } ?: throw Exception("Breed not found")

    suspend fun toggleFavorite(id: String, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        localDataSource.updateFavorite(id, isFavorite)
        runCatching {
            if (isFavorite) favoriteDao.insertFavorite(Favorite(id))
            else favoriteDao.deleteFavoriteById(id)
        }
        return@withContext getBreed(id, false)
    }


    fun getFavorites(): Flow<PagingData<Breed>> = Pager(
        pagingSourceFactory = { localDataSource.getFavorites() },
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            prefetchDistance = 5,
            enablePlaceholders = true,
        ),
    ).flow

    private suspend fun getAndUpdateBreedFromRemote(id: String, localBreed: Breed?) =
        runCatching { api.getBreed(id) }.onSuccess {
            localDataSource.updateBreed(
                it.copy(
                    isFavorite = localBreed?.isFavorite ?: false,
                    image = localBreed?.image ?: it.image
                )
            )
        }
}
