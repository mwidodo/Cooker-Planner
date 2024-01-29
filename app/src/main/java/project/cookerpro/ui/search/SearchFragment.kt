package project.cookerpro.ui.search

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import project.cookerpro.R
import project.cookerpro.adapter.IngredientAdapter
import project.cookerpro.adapter.RecipeAdapter
import project.cookerpro.data.Recipe
import project.cookerpro.databinding.FragmentSearchBinding
import project.cookerpro.ui.home.HomeFragmentDirections
import project.cookerpro.ui.recipe.RecipeDetailsFragment
import project.cookerpro.utils.OnRecipeItemClickListener


class SearchFragment : Fragment(), OnRecipeItemClickListener {

    private var _binding: FragmentSearchBinding? = null
    private var progressDialog: ProgressDialog? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        progressDialog = ProgressDialog(context).apply {
            setTitle(R.string.please_wait)
            setMessage(getString(R.string.searching_recipe))
        }
        val adapter = RecipeAdapter(this@SearchFragment)
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(context)

        val ingredientAdapter = IngredientAdapter(object : IngredientAdapter.ItemClickListener {
            override fun onItemClick(item: String) {
                searchViewModel.onSearch(item)
            }
        })
        binding.recyclerviewHistory.adapter = ingredientAdapter
        binding.recyclerviewHistory.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch {
            searchViewModel.recipes.flowWithLifecycle(
                lifecycle = lifecycle, minActiveState = Lifecycle.State.STARTED
            ).collect {
                adapter.setData(it)
            }
        }

        searchViewModel.history.observe(viewLifecycleOwner) {
            binding.recentHistory.isVisible = it.isNotEmpty()
            ingredientAdapter.setData(it)
        }

        searchViewModel.loading.observe(viewLifecycleOwner) {
            if (it) {
                progressDialog?.show()
            } else {
                progressDialog?.hide()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchViewModel.onSearch(query)
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                return false
            }
        })
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
        searchViewModel.onFavouriteClick(recipe)
    }
}