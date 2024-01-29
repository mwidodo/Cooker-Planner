package project.cookerpro.ui.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import project.cookerpro.R
import project.cookerpro.adapter.IngredientAdapter
import project.cookerpro.adapter.RecipeStepAdapter
import project.cookerpro.data.Recipe
import project.cookerpro.databinding.FragmentRecipeDetailsBinding
import project.cookerpro.utils.makeVisible

class RecipeDetailsFragment : Fragment() {

    private lateinit var recipe: Recipe

    private val args: RecipeDetailsFragmentArgs by navArgs()

    private var _binding: FragmentRecipeDetailsBinding? = null
    val recipeViewModel: RecipeViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRecipeDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recipe = args.recipe
        setUpViews()
        recipeViewModel.noOfView.observe(viewLifecycleOwner){
            binding.tvViewCount.text = getString(R.string.view_count, it)
        }

        recipeViewModel.loadRecipeData(recipe.id)
        recipeViewModel.saveRecipeData(recipe.id)
    }

    private fun setUpViews() {
        binding.apply {

            Glide.with(this@RecipeDetailsFragment).load(recipe.imgUrl).into(imageView)

            tvDescription.text = recipe.description

            tvPrepTime.text = recipe.prepTime
            tvCookTime.text = recipe.cookTime
            tvTotalTime.text = recipe.totalTime

            recipe.additionalTime?.let {
                tvAdditionalTime.text = it

                tvDescAdditionalTime.makeVisible()
                tvAdditionalTime.makeVisible()
            }

            rvIngredients.adapter = IngredientAdapter().apply {
                setData(recipe.ingredients)
            }

            tvCalories.text = recipe.calories
            tvFat.text = recipe.fat
            tvCarbs.text = recipe.carbs
            tvProtein.text = recipe.protein

            rvRecipeSteps.adapter = RecipeStepAdapter().apply {
                setData(recipe.steps)
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }


    companion object {
        fun getInstance(recipe: Recipe): RecipeDetailsFragment{
            return RecipeDetailsFragment().apply {
                arguments = RecipeDetailsFragmentArgs(recipe).toBundle()
            }
        }
    }
}