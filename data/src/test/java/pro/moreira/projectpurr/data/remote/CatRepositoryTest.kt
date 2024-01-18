package pro.moreira.projectpurr.data.remote

import androidx.room.withTransaction
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import pro.moreira.projectpurr.common.BaseTest
import pro.moreira.projectpurr.data.entities.RemoteKey
import pro.moreira.projectpurr.data.local.AppDatabase
import pro.moreira.projectpurr.data.local.CatLocalDataSource
import pro.moreira.projectpurr.data.local.dao.FavoriteDao
import pro.moreira.projectpurr.data.local.dao.RemoteKeyDao

@OptIn(ExperimentalCoroutinesApi::class)
class CatRepositoryTest : BaseTest() {

    private val api: CatApi = mockk()
    private val database: AppDatabase = mockk(relaxed = true)
    private val localDataSource: CatLocalDataSource = mockk(relaxed = true)
    private val remoteKeyDao: RemoteKeyDao = mockk(relaxed = true)
    private val favoriteDao: FavoriteDao = mockk(relaxed = true)
    private val catRepository =
        CatRepository(api, database, localDataSource, remoteKeyDao, favoriteDao)

    @Before
    fun setup() {
        mockkStatic("androidx.room.RoomDatabaseKt")
    }

    @Test
    fun `Given a query, when getCatList is called, then breeds are retrieved`() = runTest {
        mockTransaction<RemoteKey>()
        coEvery { remoteKeyDao.getKey(any()) } returns null
        val query = faker.cat().breed()
        val job = launch { catRepository.getCatList(query).collect() }
        advanceUntilIdle()
        verify { localDataSource.getBreeds(query) }
        job.cancel()
    }

    @Test
    fun `Given a breed id and refresh status, when getBreed is called, then breed is retrieved`() =
        runTest {
            val id = faker.cat().breed()
            val refresh = true
            catRepository.getBreed(id, refresh)
            coVerify { localDataSource.getBreed(id) }
        }

    @Test
    fun `Given a breed id and favorite status, when toggleFavorite is called, then breed's favorite status is updated`() =
        runTest {
            val id = faker.cat().breed()
            val isFavorite = faker.bool().bool()
            catRepository.toggleFavorite(id, isFavorite)
            coVerify { localDataSource.updateFavorite(id, isFavorite) }
        }

    @Test
    fun `When getFavorites is called, then favorite breeds are retrieved`() = runTest {
        val job = launch { catRepository.getFavorites().collect() }
        advanceUntilIdle()
        verify { localDataSource.getFavorites() }
        job.cancel()
    }

    private inline fun <reified T : Any?> mockTransaction() {
        val transactionLambda = slot<suspend () -> T>()
        coEvery { database.withTransaction(capture(transactionLambda)) } coAnswers {
            transactionLambda.captured.invoke()
        }
    }
}