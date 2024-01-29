package project.cookerpro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import project.cookerpro.R
import project.cookerpro.data.Recipe
import project.cookerpro.databinding.ItemRecipeBinding
import project.cookerpro.utils.OnRecipeItemClickListener

class RecipeAdapter constructor(
    private val listener: OnRecipeItemClickListener
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
    private val recipes = ArrayList<Recipe>()

    inner class RecipeViewHolder constructor(val binding: ItemRecipeBinding) :
        ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        return RecipeViewHolder(
            ItemRecipeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount() = recipes.size

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[holder.bindingAdapterPosition]
        holder.binding.apply {
            tvName.text = recipe.name
            tvDescription.text = recipe.description

            Glide.with(root).load(recipe.imgUrl).into(imageView)
            root.setOnClickListener {
                listener.onRecipeItemClicked(recipe)
            }

            imgFavourite?.setImageResource(if (recipe.isFavourite == true) R.drawable.ic_favorite_filled else R.drawable.ic_favourite)
            imgFavourite?.setOnClickListener {
                listener.onFavouriteClicked(recipe)
            }
        }
    }

    fun setData(list: List<Recipe>) {
        recipes.clear()
        recipes.addAll(list)
        notifyDataSetChanged()
    }
}