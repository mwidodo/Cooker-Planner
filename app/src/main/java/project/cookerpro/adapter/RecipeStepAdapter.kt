package project.cookerpro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import project.cookerpro.R
import project.cookerpro.data.Recipe
import project.cookerpro.data.RecipeStep
import project.cookerpro.databinding.ItemRecipeStepBinding


class RecipeStepAdapter: RecyclerView.Adapter<RecipeStepAdapter.RecipeStepViewHolder>() {
    private val recipeSteps = ArrayList<RecipeStep>()

    inner class RecipeStepViewHolder constructor(val binding: ItemRecipeStepBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeStepViewHolder {
        return RecipeStepViewHolder(
            ItemRecipeStepBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount() = recipeSteps.size

    override fun onBindViewHolder(holder: RecipeStepViewHolder, position: Int) {
        val step = recipeSteps[holder.bindingAdapterPosition]
        holder.binding.apply {
            tvStepCount.text = root.context.getString(R.string.step_count, position + 1)
            tvStepDesc.text = step.desc

            Glide.with(root).load(step.imgUrl).into(stepImageView)
        }
    }

    fun setData(list: List<RecipeStep>){
        recipeSteps.clear()
        recipeSteps.addAll(list)
        notifyDataSetChanged()
    }

    fun getData(): List<RecipeStep> = recipeSteps
}