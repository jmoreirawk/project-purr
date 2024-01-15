package pro.moreira.projectpurr.feature.details

import pro.moreira.projectpurr.data.entities.Breed

sealed interface DetailsScreenState {
    data object Loading : DetailsScreenState
    data class Success(val details: Breed) : DetailsScreenState
    data class Error(val message: String? = null) : DetailsScreenState
}