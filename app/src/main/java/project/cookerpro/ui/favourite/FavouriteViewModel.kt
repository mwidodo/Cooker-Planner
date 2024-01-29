package project.cookerpro.ui.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import project.cookerpro.data.DataSource
import project.cookerpro.data.Recipe

class FavouriteViewModel : ViewModel() {

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes = _recipes.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            DataSource.getFavouriteRecipes().collect {
                _recipes.value = it
            }
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