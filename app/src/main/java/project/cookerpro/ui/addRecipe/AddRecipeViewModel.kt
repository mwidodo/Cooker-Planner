package project.cookerpro.ui.addRecipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import project.cookerpro.data.DataSource
import project.cookerpro.data.Recipe

class AddRecipeViewModel : ViewModel() {

    private val _ingredient = MutableLiveData<List<String>>(emptyList())
    val ingredient: LiveData<List<String>> = _ingredient

    private val _steps = MutableLiveData<List<String>>(emptyList())
    val steps: LiveData<List<String>> = _steps

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading
    var path: String? = null

    fun addIngredient(value: String){
        _ingredient.value = _ingredient.value?.toMutableList()?.apply {
            add(value)
        }
    }

    fun addStep(value: String){
        _steps.value = _steps.value?.toMutableList()?.apply {
            add(value)
        }
    }

    fun addRecipe(recipe: Recipe, onComplete: ()->Unit){
        viewModelScope.launch {
            _loading.postValue(true)
            if(DataSource.addRecipe(recipe)){
                onComplete.invoke()
            }
            _loading.postValue(false)
        }
    }
}