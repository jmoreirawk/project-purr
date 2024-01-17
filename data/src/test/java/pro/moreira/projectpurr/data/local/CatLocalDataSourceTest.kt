package pro.moreira.projectpurr.data.local

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import pro.moreira.projectpurr.common.BaseTest
import pro.moreira.projectpurr.common.factories.BreedFactory
import pro.moreira.projectpurr.data.local.dao.BreedDao

class CatLocalDataSourceTest : BaseTest() {

    private val breedDao: BreedDao = mockk(relaxed = true)
    private val catLocalDataSource by lazy { CatLocalDataSource(breedDao) }
    private val breedFactory = BreedFactory()

    @Test
    fun `Given a list of breeds, when insertBreeds is called, then breeds are inserted`() =
        runTest {
            val breeds = breedFactory.buildList()
            catLocalDataSource.insertBreeds(breeds)
            coVerify { breedDao.insert(breeds) }
        }

    @Test
    fun `Given a breed, when updateBreed is called, then breed is updated`() = runTest {
        val breed = breedFactory.build()
        catLocalDataSource.updateBreed(breed)
        coVerify { breedDao.update(breed) }
    }

    @Test
    fun `Given a breed name, when getBreeds is called, then breeds are filtered by name`() =
        runTest {
            val name = faker.name().name()
            catLocalDataSource.getBreeds(name)
            coVerify { breedDao.filterByName(name) }
        }

    @Test
    fun `Given a breed id, when getBreed is called, then breed is retrieved by id`() = runTest {
        val id = faker.idNumber().valid()
        catLocalDataSource.getBreed(id)
        coVerify { breedDao.get(id) }
    }

    @Test
    fun `Given a breed id and favorite status, when updateFavorite is called, then breed's favorite status is updated`() =
        runTest {
            val id = faker.idNumber().valid()
            val isFavorite = faker.bool().bool()
            catLocalDataSource.updateFavorite(id, isFavorite)
            coVerify { breedDao.updateFavorite(id, isFavorite) }
        }

    @Test
    fun `When getFavorites is called, then favorite breeds are retrieved`() = runTest {
        catLocalDataSource.getFavorites()
        coVerify { breedDao.getFavorites() }
    }

    @Test
    fun `When deleteAll is called, then all breeds are deleted`() = runTest {
        catLocalDataSource.deleteAll()
        coVerify { breedDao.deleteAll() }
    }
}