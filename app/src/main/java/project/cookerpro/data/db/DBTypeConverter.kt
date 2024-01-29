package project.cookerpro.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import project.cookerpro.data.RecipeStep

class DateConverter {
    @TypeConverter
    fun toJson(list: List<String>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromJson(json: String): List<String> {
        val token = object : TypeToken<List<String>>() {}
        return Gson().fromJson(json, token.type)
    }

    @TypeConverter
    fun toRecipeStepJson(list: List<RecipeStep>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromRecipeStepJson(json: String): List<RecipeStep> {
        val token = object : TypeToken<List<RecipeStep>>() {}
        return Gson().fromJson(json, token.type)
    }
}