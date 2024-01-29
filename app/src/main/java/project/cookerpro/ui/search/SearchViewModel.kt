package project.cookerpro.ui.search

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

class SearchViewModel : ViewModel() {

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes = _recipes.asStateFlow()
    private val _history = MutableLiveData<List<String>>(emptyList())
    val history: LiveData<List<String>> = _history
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    init {
        viewModelScope.launch {
            DataSource.getSearchHistory()?.collect {
                _history.postValue(it)
            }
        }
    }


    fun onSearch(key: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            DataSource.saveSearchHistory(key)
            _recipes.value = DataSource.getRecipes(key).shuffled()
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