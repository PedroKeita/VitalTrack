package com.vitaltrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gps_tracking")
data class GpsTrackingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val durationSeconds: Long,
    val distanceMeters: Float,
    val avgSpeedKmh: Float,
    val polylinePoints: String
)