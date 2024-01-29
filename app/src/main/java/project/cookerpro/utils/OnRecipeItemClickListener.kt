package project.cookerpro.utils

import project.cookerpro.data.Recipe

interface OnRecipeItemClickListener {
    fun onRecipeItemClicked(recipe: Recipe)
    fun onFavouriteClicked(recipe: Recipe)
}