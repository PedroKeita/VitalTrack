package com.vitaltrack.app.ui.activity_module

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vitaltrack.app.data.local.entity.GpsTrackingEntity
import com.vitaltrack.app.databinding.ItemTrackingHistoryBinding

class HistoryAdapter : ListAdapter<GpsTrackingEntity, HistoryAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemTrackingHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GpsTrackingEntity) {
            val distanceKm = item.distanceMeters / 1000f
            val minutes = item.durationSeconds / 60
            val seconds = item.durationSeconds % 60

            binding.tvDate.text = item.date
            binding.tvDistance.text = "%.2f km".format(distanceKm)
            binding.tvDuration.text = "%02d:%02d min".format(minutes, seconds)
            binding.tvSpeed.text = "%.1f km/h".format(item.avgSpeedKmh)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTrackingHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<GpsTrackingEntity>() {
        override fun areItemsTheSame(a: GpsTrackingEntity, b: GpsTrackingEntity) = a.id == b.id
        override fun areContentsTheSame(a: GpsTrackingEntity, b: GpsTrackingEntity) = a == b
    }
}