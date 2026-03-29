package com.vitaltrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep")
data class SleepEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val startTime: String,
    val endTime: String?,
    val durationMinutes: Int?,
    val agitationCount: Int = 0
)