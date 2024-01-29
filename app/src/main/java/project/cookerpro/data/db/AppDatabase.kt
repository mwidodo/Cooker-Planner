package project.cookerpro.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import project.cookerpro.data.Recipe

@Database(entities = [Recipe::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao


    companion object {
        private var db: AppDatabase? = null
        fun getInstance(context: Context? = null): AppDatabase {
            if (db == null) {
                db = Room.databaseBuilder(context!!, AppDatabase::class.java, "recipes.db")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return db!!
        }
    }
}