package pro.moreira.projectpurr.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.moreira.projectpurr.data.entities.Breed
import pro.moreira.projectpurr.data.entities.RemoteKey
import pro.moreira.projectpurr.data.local.AppDatabase
import pro.moreira.projectpurr.data.local.CatLocalDataSource
import pro.moreira.projectpurr.data.local.dao.FavoriteDao
import pro.moreira.projectpurr.data.local.dao.RemoteKeyDao
import pro.moreira.projectpurr.data.remote.CatApi
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class CatsRemoteMediator(
    private val query: String,
    private val database: AppDatabase,
    private val localDataSource: CatLocalDataSource,
    private val remoteKeyDao: RemoteKeyDao,
    private val favoriteDao: FavoriteDao,
    private val api: CatApi,
) : RemoteMediator<Int, Breed>() {

    override suspend fun initialize(): InitializeAction {
        val remoteKey = database.withTransaction {
            remoteKeyDao.getKey(query)
        } ?: return InitializeAction.LAUNCH_INITIAL_REFRESH

        val cacheTimeout = TimeUnit.DAYS.convert(30, TimeUnit.MILLISECONDS)

        return if (System.currentTimeMillis() - remoteKey.lastUpdate >= cacheTimeout) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Breed>,
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(true)
                LoadType.APPEND -> {
                    val remoteKey = database.withTransaction {
                        remoteKeyDao.getKey(query)
                    } ?: return MediatorResult.Success(true)
                    remoteKey.nextKey
                }
            }

            val response = getRemoteResponse(
                limit = state.config.pageSize,
                loadKey = loadKey,
                favorites = getFavorites()
            )
            database.withTransaction {
                deleteEntriesIfNecessary(loadType)
                updateRemoteKeys(if (response.isEmpty()) null else loadKey?.plus(1))
                updateLocalBreeds(response)
            }
            MediatorResult.Success(endOfPaginationReached = response.isEmpty())
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteResponse(limit: Int, loadKey: Int?, favorites: List<String>) =
        if (query.isEmpty()) {
            api.getCatList(limit, loadKey ?: 0)
        } else {
            api.searchBreed(query, limit, loadKey ?: 0)
        }.map {
            if (it.image == null) it.getImageIfPossible()
            it.copy(isFavorite = favorites.contains(it.id))
        }

    private suspend fun getFavorites(): List<String> = withContext(Dispatchers.IO) {
        try {
            favoriteDao.getFavorites().map { it.breedId }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private suspend fun Breed.getImageIfPossible(): Breed = withContext(Dispatchers.IO) {
        runCatching { api.getImagesByBreedId(id).random() }.getOrNull()?.let { image ->
            copy(image = image).also { localDataSource.updateBreed(it) }
        } ?: this@getImageIfPossible
    }

    private suspend fun deleteEntriesIfNecessary(loadType: LoadType) {
        if (loadType == LoadType.REFRESH) {
            localDataSource.deleteAll()
            remoteKeyDao.deleteKeys()
        }
    }

    private suspend fun updateRemoteKeys(newLoadKey: Int?) {
        remoteKeyDao.insertKey(
            RemoteKey(
                id = if (query.isEmpty()) 0 else 1,
                label = query,
                nextKey = newLoadKey,
                lastUpdate = System.currentTimeMillis()
            )
        )
    }

    private suspend fun updateLocalBreeds(response: List<Breed>) {
        localDataSource.insertBreeds(response)
    }
}