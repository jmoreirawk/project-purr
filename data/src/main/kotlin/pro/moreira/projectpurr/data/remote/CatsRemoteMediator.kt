package pro.moreira.projectpurr.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.moreira.projectpurr.data.entities.Breed
import pro.moreira.projectpurr.data.entities.RemoteKey
import pro.moreira.projectpurr.data.local.CatLocalDataSource
import pro.moreira.projectpurr.data.local.dao.RemoteKeyDao
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class CatsRemoteMediator(
    private val name: String?,
    private val localDataSource: CatLocalDataSource,
    private val remoteKeyDao: RemoteKeyDao,
    private val api: CatApi,
) : RemoteMediator<Int, Breed>() {

    override suspend fun initialize(): InitializeAction {
        val cacheTimeout = TimeUnit.DAYS.convert(30, TimeUnit.MILLISECONDS)
        val lastUpdate = remoteKeyDao.getLastUpdate(name ?: "")
        return if (lastUpdate != null && System.currentTimeMillis() - lastUpdate >= cacheTimeout) {
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
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = remoteKeyDao.remoteKeyByQuery(name ?: "")
                    if (remoteKey.nextKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    remoteKey.nextKey
                }
            }

            val response = getRemoteResponse(limit = state.config.pageSize, loadKey = loadKey)
            deleteEntriesIfNecessary(loadType)
            updateRemoteKeys(loadKey)
            updateLocalBreeds(response)
            MediatorResult.Success(endOfPaginationReached = response.isEmpty())
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteResponse(limit: Int, loadKey: Int?) = if (name.isNullOrEmpty()) {
        api.getCatList(limit, loadKey ?: 0)
            .map { if (it.image == null) it.getImageIfPossible() else it }
    } else {
        api.searchBreed(name, limit, loadKey ?: 0)
    }

    private suspend fun Breed.getImageIfPossible(): Breed = withContext(Dispatchers.IO) {
        runCatching { api.getImagesByBreedId(id).random() }.getOrNull()?.let { image ->
            copy(image = image).also { localDataSource.updateBreed(it) }
        } ?: this@getImageIfPossible
    }

    private suspend fun deleteEntriesIfNecessary(loadType: LoadType) {
        if (loadType == LoadType.REFRESH) {
            localDataSource.deleteAll()
            remoteKeyDao.deleteByQuery(name ?: "")
        }
    }

    private suspend fun updateRemoteKeys(loadKey: Int?) {
        remoteKeyDao.insertOrReplace(
            RemoteKey(
                label = name ?: "",
                nextKey = (loadKey ?: 0) + 1,
                lastUpdate = System.currentTimeMillis()
            )
        )
    }

    private suspend fun updateLocalBreeds(response: List<Breed>) {
        localDataSource.insertBreeds(response)
    }
}