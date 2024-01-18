package pro.moreira.projectpurr.feature.list.favorites

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
import pro.moreira.projectpurr.feature.list.common.ListScreenState

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest : BaseTest() {

    private val repository: CatRepository = mockk(relaxed = true)
    private val viewModel by lazy { FavoritesViewModel(repository) }

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
    fun `Given an error, when onFavoriteClicked is called, then error is received`() = runTest {
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