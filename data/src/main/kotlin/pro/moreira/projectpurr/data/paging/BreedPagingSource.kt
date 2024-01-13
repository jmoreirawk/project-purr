package pro.moreira.projectpurr.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import pro.moreira.projectpurr.data.models.Breed
import pro.moreira.projectpurr.data.remote.CatApi

class BreedPagingSource(
    private val api: CatApi,
) : PagingSource<Int, Breed>() {

    companion object {
        const val PAGE_SIZE = 20
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Breed> =
        try {
            val currentPage = params.key ?: 0
            val breeds = api.getCatList(limit = PAGE_SIZE, page = currentPage)
            LoadResult.Page(
                data = breeds,
                prevKey = if (currentPage == 0) null else currentPage.minus(1),
                nextKey = if (breeds.isEmpty()) null else currentPage.plus(1),
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, Breed>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
}