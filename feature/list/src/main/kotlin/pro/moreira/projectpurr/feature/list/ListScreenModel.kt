package pro.moreira.projectpurr.feature.list

data class ListScreenModel(
    val id: String,
    val breedName: String,
    val url: String,
    val isFavorite: Boolean,
    val lifeSpan: String,
)