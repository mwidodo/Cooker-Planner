package project.cookerpro.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import project.cookerpro.data.Recipe

@Dao
interface RecipeDao {

    @Query("SELECT * FROM favourite_recipe")
    fun getAllFavouriteRecipe(): Flow<List<Recipe>>

    @Query("SELECT id FROM favourite_recipe")
    fun getAllFavouriteRecipeIds(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavouriteRecipe(recipe: Recipe): Long

    @Delete
    suspend fun deleteFavouriteRecipe(recipe: Recipe)
}