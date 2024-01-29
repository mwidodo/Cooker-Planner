package project.cookerpro.ui.recipe

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

class RecipeViewModel : ViewModel() {


    private val _noOfView = MutableLiveData(0)
    val noOfView: LiveData<Int> = _noOfView


    fun loadRecipeData(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            DataSource.getRecipeViewCount(id)?.collect {
                _noOfView.postValue(it)
            }
        }
    }

    fun saveRecipeData(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            DataSource.saveRecipeViewCount(id)
        }
    }
}