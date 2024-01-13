package pro.moreira.projectpurr.data.remote

import pro.moreira.projectpurr.data.models.Breed
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CatApi {

    @GET("breeds")
    suspend fun getCatList(
        @Query("limit") limit: Int,
        @Query("page") page: Int,
    ): List<Breed>

    @GET("breeds/search")
    suspend fun searchBreed(@Query("q") breed: String): List<Breed>

    @GET("breeds/{breed_id}")
    suspend fun getBreed(@Path("breed_id") id: String): Breed
}