package project.cookerpro.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import project.cookerpro.data.api.MyApi
import project.cookerpro.data.db.AppDatabase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


object DataSource {

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "private")
    val SEARCH_HISTORY = stringPreferencesKey("search_history")
    val RECIPE_VIEW_COUNT = stringPreferencesKey("recipe_view_count")
    val DARK_MODE = booleanPreferencesKey("dark_mode")
    private var appContext: Context? = null
    private var myApi: MyApi? = null

    fun initialize(appContext: Context) {
        this.appContext = appContext
        AppDatabase.getInstance(appContext)
        myApi = Retrofit.Builder()
            .baseUrl("http://192.241.159.24:8081/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor {
                        Log.i("OK-HTTP", it)
                    }.setLevel(HttpLoggingInterceptor.Level.BODY))
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build()
            )
            .build().run {
                create(MyApi::class.java)
            }
    }

    private suspend fun loadRecipes(key: String): List<Recipe> = withContext(Dispatchers.IO) {
        val gson = Gson()
        val response = myApi?.getRecipes(key)
        val allRecipe = ArrayList<Recipe>()
        if (response?.code() == 200) {
            val json = JSONObject(response.body()!!.toString())
            val recipes = json.getJSONArray("recipes")
            for (i in 0 until recipes.length()) {
                val data = recipes.getJSONObject(i)
                val recipe = gson.fromJson(data.getString("description"), Recipe::class.java)
                allRecipe.add(
                    recipe.copy(
                        id = data.getString("_id"),
                        imgUrl = "http://192.241.159.24:8081" + data.getString("image"),
                    )
                )
            }
        } else
            Log.e("####", "error: ${response?.errorBody()?.byteString()?.utf8()}")
        return@withContext transformFavouriteData(allRecipe)
    }


    private fun getRecipesJsonDataFromAsset(): String {
        return appContext!!.assets.open("recipes.json").bufferedReader().use { it.readText() }
    }

    suspend fun getRecipes(key: String = "") = try {
        loadRecipes(key)
    } catch (e: Exception) {
        Log.e("DataSource", "getRecipes: $e")
        emptyList()
    }

    fun getFavouriteRecipes() =
        AppDatabase.getInstance().recipeDao().getAllFavouriteRecipe()
            .map { it.map { it.copy(isFavourite = true) } }

    private fun transformFavouriteData(list: List<Recipe>): List<Recipe> {
        val favourites = AppDatabase.getInstance().recipeDao().getAllFavouriteRecipeIds()
        return list.map { it.copy(isFavourite = favourites.contains(it.id)) }
    }

    suspend fun onFavouriteClick(recipe: Recipe) = with(AppDatabase.getInstance().recipeDao()) {
        if (addFavouriteRecipe(recipe) <= 0) {
            deleteFavouriteRecipe(recipe)
        }
    }

    suspend fun addRecipe(recipe: Recipe): Boolean = withContext(Dispatchers.IO) {

        val gson = Gson()
        val file = File(recipe.imgUrl)
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
        val response = myApi?.addRecipes(
            RequestBody.create(MultipartBody.FORM, recipe.name),
            RequestBody.create(MultipartBody.FORM, gson.toJson(recipe)),
            body,
        )
        response?.code() == 201
    }

    suspend fun saveSearchHistory(key: String) {
        val gson = Gson()
        appContext?.dataStore?.edit {
            val json = it[SEARCH_HISTORY]
            val queue = ArrayList<String>()
            if (json != null) {
                queue.addAll(gson.fromJson(json, Array<String>::class.java))
            }
            queue.add(key)
            queue.reverse()
            it[SEARCH_HISTORY] = gson.toJson(queue.toSet().filterIndexed { index, s -> index < 3 })
        }
    }

    fun getSearchHistory() = appContext?.dataStore?.data?.mapNotNull {
        it[SEARCH_HISTORY]
    }?.map {
        val gson = Gson()
        gson.fromJson(it, Array<String>::class.java).toList()
    }

    suspend fun saveRecipeViewCount(id: String) {
        val gson = Gson()
        appContext?.dataStore?.edit {
            val json = it[RECIPE_VIEW_COUNT]
            val type = object : TypeToken<HashMap<String, Int>>() {}.type
            val counts =
                if (json == null) {
                    HashMap<String, Int>()
                } else
                    gson.fromJson(json, type)
            counts[id] = counts.getOrDefault(id, 0) + 1
            it[RECIPE_VIEW_COUNT] = gson.toJson(counts)
        }
    }

    fun getRecipeViewCount(id: String) = appContext?.dataStore?.data?.mapNotNull {
        it[RECIPE_VIEW_COUNT]
    }?.map {
        val type = object : TypeToken<HashMap<String, Int>>() {}.type
        val gson = Gson()
        gson.fromJson<HashMap<String, Int>>(it, type).getOrDefault(id, 0)
    }

    suspend fun saveDarkMode(isDarkMode: Boolean) {
        appContext?.dataStore?.edit {
            it[DARK_MODE] = isDarkMode
        }
    }

    suspend fun isDarkMode(): Boolean{
        return appContext?.dataStore?.data?.map { it[DARK_MODE] }?.firstOrNull() ?: false
    }
}
