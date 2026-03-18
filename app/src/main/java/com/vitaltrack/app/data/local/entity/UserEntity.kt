package com.vitaltrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey
    val id: Int = 1,
    val name: String,
    val age: Int,
    val weight: Float,
    val height: Float,
    val activityLevel: String,
    val dailyWaterGoalMl: Int,
    val dailyStepsGoal: Int,
    val dailySleepGoalHours: Float,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
