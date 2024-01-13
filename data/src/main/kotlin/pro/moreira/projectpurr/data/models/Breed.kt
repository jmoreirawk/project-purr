package pro.moreira.projectpurr.data.models

import com.squareup.moshi.Json

data class Breed(
    val id: String,
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
            "${lifeSpan[2]} years"
        }
    }
}