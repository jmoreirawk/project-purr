package pro.moreira.projectpurr.feature.list

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import pro.moreira.projectpurr.common.BaseTest
import pro.moreira.projectpurr.data.remote.CatRepository

@OptIn(ExperimentalCoroutinesApi::class)
class ListViewModelTest : BaseTest() {

    private val repository: CatRepository = mockk(relaxed = true)
    private val viewModel by lazy { ListViewModel(repository) }

    @Test
    fun `Given a query, when onSearch is called, then repository is called`() = runTest {
        val query = faker.lorem().sentence()
        viewModel.onSearch(query)
        advanceUntilIdle()
        coVerify { repository.getCatList(query) }
    }

    @Test
    fun `Given a query, when onSearch is called, then breeds are retrieved`() = runTest {
        val query = faker.lorem().sentence()
        viewModel.onSearch(query)
        advanceUntilIdle()
        viewModel.uiState.test {
            val result = awaitItem()
            assert(result is ListScreenState.Success)
        }
    }

    @Test
    fun `Given a breed id, when onFavoriteClicked is called, then repository is called`() =
        runTest {
            val id = faker.lorem().sentence()
            val isFavorite = faker.bool().bool()
            viewModel.onFavoriteClicked(id, isFavorite)
            advanceUntilIdle()
            coVerify { repository.toggleFavorite(id, isFavorite) }
        }

    @Test
    fun `Given a breed id, when onFavoriteClicked is called, then breed is retrieved`() =
        runTest {
            val id = faker.lorem().sentence()
            val isFavorite = faker.bool().bool()
            viewModel.onFavoriteClicked(id, isFavorite)
            advanceUntilIdle()
            viewModel.uiState.test {
                val result = awaitItem()
                assert(result is ListScreenState.Success)
            }
        }

    @Test
    fun `Given an error, when onFavoriteClicked is called, then error is retrieved`() = runTest {
        val id = faker.lorem().sentence()
        val isFavorite = faker.bool().bool()
        val errorMessage = faker.lorem().sentence()
        coEvery { repository.toggleFavorite(any(), any()) } throws Exception(errorMessage)
        viewModel.onFavoriteClicked(id, isFavorite)
        advanceUntilIdle()
        viewModel.errorState.test {
            assertEquals(errorMessage, awaitItem() as String)
        }
    }

    @Test
    fun `Given an error, when clearError is called, then error is cleared`() = runTest {
        val errorMessage = faker.lorem().sentence()
        coEvery { repository.toggleFavorite(any(), any()) } throws Exception(errorMessage)
        viewModel.onFavoriteClicked("", false)
        advanceUntilIdle()
        viewModel.errorState.test {
            assert(awaitItem() == errorMessage)
            viewModel.clearError()
            assert(awaitItem() == null)
        }
    }
}