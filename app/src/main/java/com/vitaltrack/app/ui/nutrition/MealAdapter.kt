package com.vitaltrack.app.ui.nutrition

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vitaltrack.app.data.local.entity.MealEntity
import com.vitaltrack.app.databinding.ItemMealBinding

class MealAdapter(
    private val onDelete: (MealEntity) -> Unit
) : ListAdapter<MealEntity, MealAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemMealBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MealEntity) {
            binding.tvDescription.text = item.description
            binding.tvCategory.text = item.category
            binding.tvCalories.text = "${item.calories} kcal"
            binding.tvTime.text = item.time
            binding.tvMacros.text = "P: ${item.protein}g | C: ${item.carbs}g | G: ${item.fat}g"
            binding.btnDelete.setOnClickListener { onDelete(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMealBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<MealEntity>() {
        override fun areItemsTheSame(a: MealEntity, b: MealEntity) = a.id == b.id
        override fun areContentsTheSame(a: MealEntity, b: MealEntity) = a == b
    }
}