package pro.moreira.projectpurr.data.local

import pro.moreira.projectpurr.data.entities.Breed
import pro.moreira.projectpurr.data.local.dao.BreedDao
import javax.inject.Inject

class CatLocalDataSource
@Inject constructor(private val breedDao: BreedDao) {

    suspend fun insertBreeds(breeds: List<Breed>) = breedDao.insert(breeds)

    suspend fun updateBreed(breed: Breed) = breedDao.update(breed)

    fun getBreeds(name: String) =
        if (name.isEmpty()) breedDao.getAll() else breedDao.filterByName(name)

    suspend fun getBreed(id: String) = breedDao.get(id)

    suspend fun updateFavorite(id: String, isFavorite: Boolean) =
        breedDao.updateFavorite(id, isFavorite)

    fun getFavorites() = breedDao.getFavorites()

    suspend fun deleteAll() = breedDao.deleteAll()
}