package com.vitaltrack.app.data.local.dao

import androidx.room.*
import com.vitaltrack.app.data.local.entity.SleepEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDao {

    @Insert
    suspend fun insert(sleep: SleepEntity): Long

    @Update
    suspend fun update(sleep: SleepEntity)

    @Query("SELECT * FROM sleep WHERE endTime IS NOT NULL ORDER BY id DESC LIMIT 1")
    fun getLatest(): Flow<SleepEntity?>

    @Query("SELECT * FROM sleep ORDER BY date DESC LIMIT 7")
    fun getLast7Days(): Flow<List<SleepEntity>>

    @Query("SELECT * FROM sleep WHERE endTime IS NULL LIMIT 1")
    suspend fun getOngoing(): SleepEntity?
}