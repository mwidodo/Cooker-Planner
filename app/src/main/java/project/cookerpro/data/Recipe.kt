package project.cookerpro.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import project.cookerpro.data.db.DateConverter

@Parcelize
@JsonClass(generateAdapter = true)
@Entity("favourite_recipe")
@TypeConverters(DateConverter::class)
data class Recipe(
    @PrimaryKey
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("img_url") val imgUrl: String,
    @SerializedName("description") val description: String,
    @SerializedName("prep_time") val prepTime: String?,
    @SerializedName("cook_time") val cookTime: String?,
    @SerializedName("total_time") val totalTime: String?,
    @SerializedName("additional_time") val additionalTime: String? = null,
    @SerializedName("calories") val calories: String,
    @SerializedName("fat") val fat: String,
    @SerializedName("carbs") val carbs: String,
    @SerializedName("protein") val protein: String,
    @SerializedName("ingredients") val ingredients: List<String>,
    @SerializedName("steps") val steps: List<RecipeStep>,
    @SerializedName("isFavourite") val isFavourite: Boolean?,
) : Parcelable
