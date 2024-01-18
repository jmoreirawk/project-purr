package pro.moreira.projectpurr.feature.details

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import pro.moreira.projectpurr.common.BaseTest
import pro.moreira.projectpurr.common.factories.BreedFactory
import pro.moreira.projectpurr.data.entities.Breed
import pro.moreira.projectpurr.data.remote.CatRepository

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest : BaseTest() {

    private val savedStateHandle: SavedStateHandle = mockk(relaxed = true)
    private val repository: CatRepository = mockk(relaxed = true)
    private val viewModel by lazy { DetailsViewModel(savedStateHandle, repository) }
    private val breedFactory = BreedFactory()
    private lateinit var breed: Breed

    @Before
    fun setup() {
        breed = breedFactory.build()
        coEvery { savedStateHandle.get<String>("id") } returns breed.id
        coEvery { repository.getBreed(any(), any()) } returns breed
    }

    @Test
    fun `Given a breed id, when viewModel is initialized, then breed is retrieved`() = runTest {
        viewModel
        advanceUntilIdle()
        viewModel.uiState.test {
            val result = awaitItem()
            assert(result is DetailsScreenState.Success)
            assertEquals(breed, (result as DetailsScreenState.Success).details)
        }
    }

    @Test
    fun `Given an error, when viewModel is initialized, then error is retrieved`() = runTest {
        val errorMessage = faker.lorem().sentence()
        coEvery { repository.getBreed(any(), any()) } throws Exception(errorMessage)
        viewModel
        advanceUntilIdle()
        viewModel.uiState.test {
            val result = awaitItem()
            assert(result is DetailsScreenState.Error)
            assertEquals(errorMessage, (result as DetailsScreenState.Error).message)
        }
    }

    @Test
    fun `Given a breed id, when onFavoriteClicked is called, then breed is retrieved`() = runTest {
        val isFavorite = faker.bool().bool()
        coEvery {
            repository.toggleFavorite(any(), any())
        } returns breed.copy(isFavorite = isFavorite)
        viewModel.onFavoriteClicked(isFavorite)
        advanceUntilIdle()
        viewModel.uiState.test {
            val result = awaitItem()
            assert(result is DetailsScreenState.Success)
            val breed = (result as DetailsScreenState.Success).details
            assertEquals(breed.isFavorite, isFavorite)
        }
    }

    @Test
    fun `Given an error, when onFavoriteClicked is called, then error is retrieved`() = runTest {
        val isFavorite = faker.bool().bool()
        val errorMessage = faker.lorem().sentence()
        coEvery {
            repository.toggleFavorite(any(), any())
        } throws Exception(errorMessage)
        viewModel.onFavoriteClicked(isFavorite)
        advanceUntilIdle()
        viewModel.uiState.test {
            val result = awaitItem()
            assert(result is DetailsScreenState.Error)
            assertEquals(errorMessage, (result as DetailsScreenState.Error).message)
        }
    }

    @Test
    fun `Given a breed id, when onRetry is called, then breed is retrieved`() = runTest {
        viewModel
        advanceUntilIdle()
        viewModel.onRetry()
        viewModel.uiState.test {
            val result = awaitItem()
            assert(result is DetailsScreenState.Success)
            assertEquals(breed, (result as DetailsScreenState.Success).details)
        }
    }

    @Test
    fun `Given a breed id, when onRetry is called, then getBreed is called with refresh true`() =
        runTest {
            viewModel
            advanceUntilIdle()
            viewModel.onRetry()
            advanceUntilIdle()
            coVerify { repository.getBreed(any(), true) }
        }
}