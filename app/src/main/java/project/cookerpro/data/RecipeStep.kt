package project.cookerpro.data

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class RecipeStep(
    @Json(name = "desc") val desc: String,
    @Json(name = "img_url") val imgUrl: String? = null,
) : Parcelable
