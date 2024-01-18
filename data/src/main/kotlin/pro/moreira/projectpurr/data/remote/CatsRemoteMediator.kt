package pro.moreira.projectpurr.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import pro.moreira.projectpurr.data.entities.Breed
import pro.moreira.projectpurr.data.entities.RemoteKey
import pro.moreira.projectpurr.data.local.CatLocalDataSource
import pro.moreira.projectpurr.data.local.dao.RemoteKeyDao

@OptIn(ExperimentalPagingApi::class)
class CatsRemoteMediator(
    private val name: String?,
    private val localDataSource: CatLocalDataSource,
    private val remoteKeyDao: RemoteKeyDao,
    private val api: CatApi,
) : RemoteMediator<Int, Breed>() {

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

    private suspend fun getRemoteResponse(limit: Int, loadKey: Int?) = if (name == null) {
        api.getCatList(limit, loadKey ?: 0)
    } else {
        api.searchBreed(name, limit, loadKey ?: 0)
    }

    private suspend fun deleteEntriesIfNecessary(loadType: LoadType) {
        if (loadType == LoadType.REFRESH) {
            localDataSource.deleteAll()
            remoteKeyDao.deleteByQuery(name ?: "")
        }
    }

    private suspend fun updateRemoteKeys(loadKey: Int?) {
        remoteKeyDao.insertOrReplace(RemoteKey(label = name ?: "", nextKey = (loadKey ?: 0) + 1))
    }

    private suspend fun updateLocalBreeds(response: List<Breed>) {
        localDataSource.insertBreeds(response)
    }
}