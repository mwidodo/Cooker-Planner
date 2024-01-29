package project.cookerpro.data.api

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import project.cookerpro.data.Recipe
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Multipart
import retrofit2.http.Part

interface MyApi {

    @GET("get-recipies")
    suspend fun getRecipes(@Query("search") key: String): Response<JsonObject>


    @Multipart
    @POST("create-recipe")
    suspend fun addRecipes(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part image: MultipartBody.Part,
    ): Response<JsonObject>
}