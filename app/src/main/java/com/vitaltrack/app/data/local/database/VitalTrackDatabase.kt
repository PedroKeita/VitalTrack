package com.vitaltrack.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vitaltrack.app.data.local.dao.GpsTrackingDao
import com.vitaltrack.app.data.local.dao.StepCounterDao
import com.vitaltrack.app.data.local.dao.UserDao
import com.vitaltrack.app.data.local.entity.GpsTrackingEntity
import com.vitaltrack.app.data.local.entity.StepCounterEntity
import com.vitaltrack.app.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        StepCounterEntity::class,
        GpsTrackingEntity::class
               ],
    version = 3,
    exportSchema = false
)

abstract class VitalTrackDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun stepCounterDao(): StepCounterDao
    abstract fun gpsTrackingDao(): GpsTrackingDao
}