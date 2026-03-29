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
}