package pro.moreira.projectpurr.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import pro.moreira.projectpurr.data.entities.Breed

@Dao
interface BreedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(breeds: List<Breed>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(breed: Breed)

    @Query("SELECT * FROM Breed")
    fun getAll(): PagingSource<Int, Breed>

    @Query("SELECT * FROM Breed WHERE id = :id")
    suspend fun get(id: String): Breed?

    @Query("SELECT * FROM Breed WHERE name LIKE '%' || :name || '%'")
    fun filterByName(name: String): PagingSource<Int, Breed>

    @Query("UPDATE Breed SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Query("DELETE FROM Breed")
    suspend fun deleteAll()
}