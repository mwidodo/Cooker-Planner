package project.cookerpro.ui.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import project.cookerpro.MobileNavigationDirections
import project.cookerpro.R
import project.cookerpro.adapter.RecipeAdapter
import project.cookerpro.data.Recipe
import project.cookerpro.databinding.FragmentHomeBinding
import project.cookerpro.databinding.FragmentRecipeBinding
import project.cookerpro.ui.home.HomeFragmentDirections
import project.cookerpro.ui.home.HomeViewModel
import project.cookerpro.ui.recipe.RecipeDetailsFragment
import project.cookerpro.utils.OnRecipeItemClickListener

class FavouriteFragment : Fragment(), OnRecipeItemClickListener {

    private var _binding: FragmentRecipeBinding? = null
    val favouriteViewModel: FavouriteViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter  = RecipeAdapter(this@FavouriteFragment)
        binding.floatingActionButton.isVisible = false
        binding.recyclerview.adapter = adapter

        lifecycleScope.launch {
            favouriteViewModel.recipes.flowWithLifecycle(
                lifecycle = lifecycle, minActiveState = Lifecycle.State.STARTED
            ).collect {
                adapter.setData(it)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRecipeItemClicked(recipe: Recipe) {
        val detailContainer = activity?.findViewById<FrameLayout>(R.id.nav_host_fragment_detail)
        if (detailContainer == null) {
            findNavController().navigate(
                HomeFragmentDirections.actionGlobalRecipeDetails(
                    recipe = recipe
                )
            )
        } else {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.nav_host_fragment_detail, RecipeDetailsFragment.getInstance(recipe))
                ?.addToBackStack(null)
                ?.commit()
        }
    }


    override fun onFavouriteClicked(recipe: Recipe) {
        favouriteViewModel.onFavouriteClick(recipe)
    }
}