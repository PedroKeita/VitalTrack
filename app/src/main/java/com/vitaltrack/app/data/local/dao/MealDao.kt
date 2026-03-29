package com.vitaltrack.app.data.local.dao

import androidx.room.*
import com.vitaltrack.app.data.local.entity.MealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    @Insert
    suspend fun insert(meal: MealEntity)

    @Delete
    suspend fun delete(meal: MealEntity)

    @Query("SELECT * FROM meal WHERE date = :date ORDER BY time DESC")
    fun getByDate(date: String): Flow<List<MealEntity>>

    @Query("SELECT SUM(calories) FROM meal WHERE date = :date")
    fun getTotalCaloriesByDate(date: String): Flow<Int?>
}