package com.vitaltrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_counter")
data class StepCounterEntity (
    @PrimaryKey
    val date: String,
    val steps: Int,
    val calories: Float
)