package com.vitaltrack.app.data.local.dao

import androidx.room.*
import com.vitaltrack.app.data.local.entity.GpsTrackingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GpsTrackingDao {

    @Insert
    suspend fun insertTracking(tracking: GpsTrackingEntity)

    @Query("SELECT * FROM gps_tracking ORDER BY date DESC")
    fun getAllTrackings(): Flow<List<GpsTrackingEntity>>
}