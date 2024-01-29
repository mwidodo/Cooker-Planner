package project.cookerpro.ui.home

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import project.cookerpro.R
import project.cookerpro.adapter.RecipeAdapter
import project.cookerpro.data.Recipe
import project.cookerpro.databinding.FragmentHomeBinding
import project.cookerpro.ui.addRecipe.AddRecipeActivity
import project.cookerpro.ui.recipe.RecipeDetailsFragment
import project.cookerpro.utils.OnRecipeItemClickListener

class HomeFragment : Fragment(), OnRecipeItemClickListener {

    private var _binding: FragmentHomeBinding? = null
    val homeViewModel: HomeViewModel by viewModels()
    private var progressDialog: ProgressDialog?=null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(context).apply {
            setTitle(R.string.please_wait)
            setMessage(getString(R.string.loading_recipe))
        }
        val adapter = RecipeAdapter(this@HomeFragment)
        binding.recyclerview.adapter = adapter

        lifecycleScope.launch {
            homeViewModel.recipes.flowWithLifecycle(
                lifecycle = lifecycle, minActiveState = Lifecycle.State.STARTED
            ).collect {
                adapter.setData(it)
            }
        }

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(context, AddRecipeActivity::class.java)
            startActivity(intent)
        }

        homeViewModel.loading.observe(viewLifecycleOwner){
            if(it){
                progressDialog?.show()
            }
            else{
                progressDialog?.hide()
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
        homeViewModel.onFavouriteClick(recipe)
    }

}