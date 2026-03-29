package com.vitaltrack.app.ui.hydration

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vitaltrack.app.data.local.entity.WaterIntakeEntity
import com.vitaltrack.app.databinding.ItemWaterIntakeBinding

class WaterIntakeAdapter(
    private val onDelete: (WaterIntakeEntity) -> Unit
) : ListAdapter<WaterIntakeEntity, WaterIntakeAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemWaterIntakeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WaterIntakeEntity) {
            binding.tvAmount.text = "${item.amountMl}ml"
            binding.tvTime.text = item.time
            binding.btnDelete.setOnClickListener { onDelete(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWaterIntakeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<WaterIntakeEntity>() {
        override fun areItemsTheSame(a: WaterIntakeEntity, b: WaterIntakeEntity) = a.id == b.id
        override fun areContentsTheSame(a: WaterIntakeEntity, b: WaterIntakeEntity) = a == b
    }
}