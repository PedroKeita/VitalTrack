package com.vitaltrack.app.data.local.dao

import androidx.room.*
import com.vitaltrack.app.data.local.entity.WaterIntakeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterIntakeDao {

    @Insert
    suspend fun insert(water: WaterIntakeEntity)

    @Query("SELECT * FROM water_intake WHERE date = :date ORDER BY time DESC")
    fun getByDate(date: String): Flow<List<WaterIntakeEntity>>

    @Query("SELECT SUM(amountMl) FROM water_intake WHERE date = :date")
    fun getTotalByDate(date: String): Flow<Int?>

    @Delete
    suspend fun delete(water: WaterIntakeEntity)

    @Query("SELECT date, SUM(amountMl) as total FROM water_intake GROUP BY date ORDER BY date DESC LIMIT :days")
    suspend fun getLast(days: Int): List<WaterDailySummary>

    data class WaterDailySummary(
        val date: String,
        val total: Int
    )
}