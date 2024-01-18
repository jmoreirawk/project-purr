package pro.moreira.projectpurr.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.room.withTransaction
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import pro.moreira.projectpurr.common.BaseTest
import pro.moreira.projectpurr.common.factories.BreedFactory
import pro.moreira.projectpurr.common.factories.ImageFactory
import pro.moreira.projectpurr.data.entities.Breed
import pro.moreira.projectpurr.data.entities.RemoteKey
import pro.moreira.projectpurr.data.local.AppDatabase
import pro.moreira.projectpurr.data.local.CatLocalDataSource
import pro.moreira.projectpurr.data.local.dao.FavoriteDao
import pro.moreira.projectpurr.data.local.dao.RemoteKeyDao
import pro.moreira.projectpurr.data.remote.CatApi

@OptIn(ExperimentalPagingApi::class)
class CatsRemoteMediatorTest : BaseTest() {

    private val api: CatApi = mockk(relaxed = true)
    private val database: AppDatabase = mockk(relaxed = true)
    private val localDataSource: CatLocalDataSource = mockk(relaxed = true)
    private val remoteKeyDao: RemoteKeyDao = mockk(relaxed = true)
    private val favoriteDao: FavoriteDao = mockk(relaxed = true)
    private lateinit var catsRemoteMediator: CatsRemoteMediator
    private val breedFactory = BreedFactory()
    private val imageFactory = ImageFactory()
    private val query = faker.cat().breed()

    @Before
    fun setup() {
        catsRemoteMediator =
            CatsRemoteMediator(query, database, localDataSource, remoteKeyDao, favoriteDao, api)
        mockkStatic("androidx.room.RoomDatabaseKt")
    }

    @Test
    fun `Given a query, when initialize is called, then lastUpdate is retrieved`() = runTest {
        mockTransaction<RemoteKey>()
        catsRemoteMediator.initialize()
        coVerify { remoteKeyDao.getKey(query) }
    }

    @Test
    fun `Given a query, when load is called, then breeds are retrieved`() = runTest {
        mockTransaction<RemoteKey>()
        val state: PagingState<Int, Breed> = mockk(relaxed = true)
        catsRemoteMediator.load(LoadType.REFRESH, state)
        coVerify { api.searchBreed(query, any(), any()) }
    }

    @Test
    fun `Given no query, when load is called, then breeds are retrieved`() = runTest {
        mockTransaction<Unit>()
        catsRemoteMediator =
            CatsRemoteMediator("", database, localDataSource, remoteKeyDao, favoriteDao, api)
        catsRemoteMediator.load(LoadType.REFRESH, mockk(relaxed = true))
        coVerify { api.getCatList(any(), any()) }
    }

    @Test
    fun `Given no query, when load is called, then breeds with no image are saved`() = runTest {
        mockTransaction<Unit>()
        catsRemoteMediator =
            CatsRemoteMediator("", database, localDataSource, remoteKeyDao, favoriteDao, api)
        val breedList = breedFactory.buildList(withImage = false)
        coEvery { api.getCatList(any(), any()) } returns breedList
        coEvery { api.getImagesByBreedId(any()) } returns imageFactory.buildList()
        catsRemoteMediator.load(LoadType.REFRESH, mockk(relaxed = true))
        coVerify { api.getCatList(any(), any()) }
        coVerify { api.getImagesByBreedId(any()) }
        coVerify { localDataSource.updateBreed(any()) }
    }

    private inline fun <reified T : Any?> mockTransaction() {
        val transactionLambda = slot<suspend () -> T>()
        coEvery { database.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }
    }
}