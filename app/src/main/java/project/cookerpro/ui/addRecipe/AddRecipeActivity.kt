package project.cookerpro.ui.addRecipe

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.atwa.filepicker.core.FilePicker
import com.bumptech.glide.Glide
import project.cookerpro.R
import project.cookerpro.adapter.IngredientAdapter
import project.cookerpro.adapter.RecipeStepAdapter
import project.cookerpro.data.Recipe
import project.cookerpro.data.RecipeStep
import project.cookerpro.databinding.ActivityAddRecipeBinding
import project.cookerpro.databinding.DialogAddIngredientBinding
import project.cookerpro.databinding.DialogAddStepBinding
import java.io.File

class AddRecipeActivity : AppCompatActivity() {
    private val addRecipeViewModel: AddRecipeViewModel by viewModels()
    private val filePicker = FilePicker.getInstance(this)
    private lateinit var binding: ActivityAddRecipeBinding
    private var ingredientAdapter: IngredientAdapter? = null
    private var stepAdapter: RecipeStepAdapter? = null
    private var progressDialog: ProgressDialog?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = ProgressDialog(this).apply {
            setTitle(R.string.please_wait)
            setMessage(getString(R.string.adding_recipe))
        }

        ingredientAdapter = IngredientAdapter()
        binding.rvIngredients.layoutManager = LinearLayoutManager(this)
        binding.rvIngredients.adapter = ingredientAdapter
        stepAdapter = RecipeStepAdapter()
        binding.rvRecipeSteps.layoutManager = LinearLayoutManager(this)
        binding.rvRecipeSteps.adapter = stepAdapter


        binding.imgAddIngredient.setOnClickListener {
            enterIngredient()
        }

        binding.imgAddDirections.setOnClickListener {
            enterSteps()
        }


        addRecipeViewModel.ingredient.observe(this) {
            ingredientAdapter?.setData(it)
        }
        addRecipeViewModel.steps.observe(this) {
            stepAdapter?.setData(it.map { RecipeStep(it, "") })
        }

        binding.addImage.setOnClickListener {
            pickFile()
        }

        binding.btnSubmit.setOnClickListener {
            validate()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            } ?: run {
                addRecipeViewModel.addRecipe(prepareRecipe()) {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }

        addRecipeViewModel.loading.observe(this){
            if(it){
                progressDialog?.show()
            }
            else{
                progressDialog?.hide()
            }
        }

        Glide.with(this).load(addRecipeViewModel.path).into(binding.imageView)
    }


    private fun prepareRecipe(): Recipe {
        return Recipe(
            "",
            binding.tvName.text.toString(),
            addRecipeViewModel.path.orEmpty(),
            binding.tvDescription.text.toString(),
            binding.tvPrepTime.text.toString(),
            binding.tvCookTime.text.toString(),
            binding.tvTotalTime.text.toString(),
            binding.tvAdditionalTime.text.toString(),
            binding.tvCalories.text.toString(),
            binding.tvFat.text.toString(),
            binding.tvCarbs.text.toString(),
            binding.tvProtein.text.toString(),
            ingredientAdapter?.getData().orEmpty(),
            stepAdapter?.getData().orEmpty(),
            false
        )
    }


    private fun enterIngredient() {
        val alert = AlertDialog.Builder(this).create()
        val binding = DialogAddIngredientBinding.inflate(layoutInflater, null, false)
        alert.setView(binding.root)
        binding.btnAdd.setOnClickListener {
            val value = binding.etIngredient.text.toString()
            if (value.isNotEmpty()) {
                addRecipeViewModel.addIngredient(value)
                alert.dismiss()
            } else {
                Toast.makeText(this, getString(R.string.enter_valid_ingredient), Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.btnCancel.setOnClickListener {
            alert.dismiss()
        }
        alert.show()
    }

    private fun enterSteps() {
        val alert = AlertDialog.Builder(this).create()
        val binding = DialogAddStepBinding.inflate(layoutInflater, null, false)
        alert.setView(binding.root)
        binding.btnAdd.setOnClickListener {
            val value = binding.etIngredient.text.toString()
            if (value.isNotEmpty()) {
                addRecipeViewModel.addStep(value)
                alert.dismiss()
            } else {
                Toast.makeText(this, getString(R.string.enter_valid_steps), Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.btnCancel.setOnClickListener {
            alert.dismiss()
        }
        alert.show()
    }


    private fun validate(): String? = with(binding) {
        if (addRecipeViewModel.path.isNullOrEmpty()) {
            return@with getString(R.string.select_image)
        }

        if (tvDescription.text.isNullOrEmpty()) {
            return@with getString(R.string.enter_valid_desc)
        }
        if (tvPrepTime.text.isNullOrEmpty()) {
            return@with getString(R.string.enter_valid_prep_time)
        }
        if (tvCookTime.text.isNullOrEmpty()) {
            return@with getString(R.string.enter_valid_cook_time)
        }
        if (tvAdditionalTime.text.isNullOrEmpty()) {
            return@with getString(R.string.enter_valid_cook_time)
        }
        if (tvTotalTime.text.isNullOrEmpty()) {
            return@with getString(R.string.enter_valid_cook_time)
        }

        if (ingredientAdapter?.itemCount == 0) {
            return@with getString(R.string.enter_valid_ingredient)
        }

        if (stepAdapter?.itemCount == 0) {
            return@with getString(R.string.enter_valid_steps)
        }

        if (tvCalories.text.isNullOrEmpty()) {
            return@with getString(R.string.enter_valid_calorie)
        }
        if (tvFat.text.isNullOrEmpty()) {
            return@with getString(R.string.enter_valid_fat)
        }
        if (tvCarbs.text.isNullOrEmpty()) {
            return@with getString(R.string.enter_valid_carbs)
        }
        if (tvProtein.text.isNullOrEmpty()) {
            return@with getString(R.string.enter_valid_protein)
        }
        return null
    }

    private fun pickFile() {
        filePicker.pickImage { meta ->
            val name: String? = meta?.name
            val file: File? = meta?.file
            if (name != null && file != null) {
                addRecipeViewModel.path = file.absolutePath
                Glide.with(this).load(addRecipeViewModel.path).into(binding.imageView)
            }
        }
    }

}
