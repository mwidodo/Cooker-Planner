package project.cookerpro.ui.home

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

class HomeViewModel : ViewModel() {

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes = _recipes.asStateFlow()

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            _recipes.value = DataSource.getRecipes().shuffled()
            _loading.postValue(false)
        }
    }

    fun onFavouriteClick(recipe: Recipe) = viewModelScope.launch {
        DataSource.onFavouriteClick(recipe)
        _recipes.value = _recipes.value.map {
            if (it.id == recipe.id)
                it.copy(isFavourite = it.isFavourite?.not())
            else
                it
        }
    }
}