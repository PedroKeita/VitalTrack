package com.vitaltrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vitaltrack.app.data.local.entity.StepCounterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StepCounterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stepCounter: StepCounterEntity)

    @Query("SELECT * FROM step_counter WHERE date = :date LIMIT 1")
    fun getStepsByDate(date: String): Flow<StepCounterEntity?>

    @Query("SELECT * FROM step_counter ORDER BY date DESC LIMIT 7")
    fun getLast7Days(): Flow<List<StepCounterEntity>>

    @Query("SELECT steps FROM step_counter WHERE date = :date LIMIT 1")
    suspend fun getStepsByDateOnce(date: String): Int?

    @Query("SELECT * FROM step_counter ORDER BY date DESC LIMIT :days")
    suspend fun getLast(days: Int): List<StepCounterEntity>
}