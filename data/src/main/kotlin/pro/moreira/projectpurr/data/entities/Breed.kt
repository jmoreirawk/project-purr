package pro.moreira.projectpurr.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity
data class Breed(
    @PrimaryKey val id: String,
    val name: String,
    val origin: String,
    val temperament: String,
    val description: String,
    val image: Image? = null,
    @Json(name = "life_span") val lifeSpan: String,
    val isFavorite: Boolean = false,
) {
    fun getMaxAverageLifeSpan(): String {
        val lifeSpan = lifeSpan.split(" - ")
        return if (lifeSpan.size == 1) {
            "${lifeSpan[0]} years"
        } else {
            "${lifeSpan[1]} years"
        }
    }
}