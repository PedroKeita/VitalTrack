package com.vitaltrack.app.ui.sleep

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vitaltrack.app.databinding.ItemSleepHistoryBinding

class SleepHistoryAdapter(
    private val onDelete: (SleepWithScore) -> Unit
) : ListAdapter<SleepWithScore, SleepHistoryAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemSleepHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SleepWithScore) {
            val hours = (item.sleep.durationMinutes ?: 0) / 60
            val mins = (item.sleep.durationMinutes ?: 0) % 60
            binding.tvDate.text = item.sleep.date
            binding.tvDuration.text = "${hours}h ${mins}min"
            binding.tvScore.text = "${item.score}"
            binding.tvClassification.text = item.classification
            binding.btnDelete.setOnClickListener { onDelete(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSleepHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<SleepWithScore>() {
        override fun areItemsTheSame(a: SleepWithScore, b: SleepWithScore) =
            a.sleep.id == b.sleep.id
        override fun areContentsTheSame(a: SleepWithScore, b: SleepWithScore) = a == b
    }
}