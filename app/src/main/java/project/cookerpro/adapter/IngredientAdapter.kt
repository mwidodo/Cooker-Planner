package project.cookerpro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import project.cookerpro.data.Recipe
import project.cookerpro.databinding.ItemIngredientBinding


class IngredientAdapter(private val listener: ItemClickListener?=null) : RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    private val ingredients=  ArrayList<String>()
    inner class IngredientViewHolder constructor(val binding: ItemIngredientBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        return IngredientViewHolder(
            ItemIngredientBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount() = ingredients.size

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.binding.tvIngredient.text = ingredients[holder.bindingAdapterPosition]
        holder.binding.root.setOnClickListener {
            listener?.onItemClick(ingredients[holder.bindingAdapterPosition])
        }
    }

    fun setData(list: List<String>){
        ingredients.clear()
        ingredients.addAll(list)
        notifyDataSetChanged()
    }

    fun getData(): List<String> = ingredients

    interface ItemClickListener{
        fun onItemClick(item: String)
    }
}