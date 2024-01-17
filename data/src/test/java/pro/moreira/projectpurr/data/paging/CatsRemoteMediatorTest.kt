package pro.moreira.projectpurr.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import pro.moreira.projectpurr.common.BaseTest
import pro.moreira.projectpurr.common.factories.BreedFactory
import pro.moreira.projectpurr.common.factories.ImageFactory
import pro.moreira.projectpurr.data.entities.Breed
import pro.moreira.projectpurr.data.local.CatLocalDataSource
import pro.moreira.projectpurr.data.local.dao.RemoteKeyDao
import pro.moreira.projectpurr.data.remote.CatApi

@OptIn(ExperimentalPagingApi::class)
class CatsRemoteMediatorTest : BaseTest() {

    private val api: CatApi = mockk(relaxed = true)
    private val localDataSource: CatLocalDataSource = mockk(relaxed = true)
    private val remoteKeyDao: RemoteKeyDao = mockk(relaxed = true)
    private lateinit var catsRemoteMediator: CatsRemoteMediator
    private val breedFactory = BreedFactory()
    private val imageFactory = ImageFactory()
    private val query = faker.cat().breed()

    @Before
    fun setup() {
        catsRemoteMediator = CatsRemoteMediator(query, localDataSource, remoteKeyDao, api)
    }

    @Test
    fun `Given a query, when initialize is called, then lastUpdate is retrieved`() = runTest {
        coEvery { remoteKeyDao.getLastUpdate(query) } returns System.currentTimeMillis()
        catsRemoteMediator.initialize()
        coVerify { remoteKeyDao.getLastUpdate(query) }
    }

    @Test
    fun `Given a query, when load is called, then breeds are retrieved`() = runTest {
        val state: PagingState<Int, Breed> = mockk(relaxed = true)
        catsRemoteMediator.load(LoadType.REFRESH, state)
        coVerify { api.searchBreed(query, any(), any()) }
    }

    @Test
    fun `Given no query, when load is called, then breeds are retrieved`() = runTest {
        catsRemoteMediator = CatsRemoteMediator("", localDataSource, remoteKeyDao, api)
        catsRemoteMediator.load(LoadType.REFRESH, mockk(relaxed = true))
        coVerify { api.getCatList(any(), any()) }
    }

    @Test
    fun `Given no query, when load is called, then breeds with no image are saved`() = runTest {
        catsRemoteMediator = CatsRemoteMediator("", localDataSource, remoteKeyDao, api)
        val breedList = breedFactory.buildList(withImage = false)
        coEvery { api.getCatList(any(), any()) } returns breedList
        coEvery { api.getImagesByBreedId(any()) } returns imageFactory.buildList()
        catsRemoteMediator.load(LoadType.REFRESH, mockk(relaxed = true))
        coVerify { api.getCatList(any(), any()) }
        coVerify { api.getImagesByBreedId(any()) }
        coVerify { localDataSource.updateBreed(any()) }
    }
}