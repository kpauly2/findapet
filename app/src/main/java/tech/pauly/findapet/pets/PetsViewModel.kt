package tech.pauly.findapet.pets

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import tech.pauly.findapet.BaseViewModel
import tech.pauly.findapet.di.CoroutineContextProvider
import tech.pauly.findapet.pets.model.Pet
import tech.pauly.findapet.pets.network.PetsRepository
import java.lang.Exception

class PetsViewModel(
    private val repository: PetsRepository,
    override val scope: CoroutineContextProvider
) : BaseViewModel(scope) {

    private val _petListState = MutableLiveData<PetListState>()
    val petListState: LiveData<PetListState>
        get() = _petListState

    fun refreshPets() {
        launch(scope.android) {
            try {
                repository.fetchPets()?.pets?.let { pets ->
                    _petListState.postValue(PetListState(pets))
                } ?: postError()
            } catch (e: Exception) {
                throw e // temp
//                postError(e.message)
            }
        }
    }

    private fun postError(message: String? = null) {
        _petListState.postValue(PetListState(error = message ?: "An unexpected error occurred"))
    }
}

class PetListState(
    val petList: List<Pet>? = null,
    val error: String? = null
)