package com.vitaltrack.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vitaltrack.app.data.local.dao.GpsTrackingDao
import com.vitaltrack.app.data.local.dao.MealDao
import com.vitaltrack.app.data.local.dao.SleepDao
import com.vitaltrack.app.data.local.dao.StepCounterDao
import com.vitaltrack.app.data.local.dao.UserDao
import com.vitaltrack.app.data.local.dao.WaterIntakeDao
import com.vitaltrack.app.data.local.entity.GpsTrackingEntity
import com.vitaltrack.app.data.local.entity.MealEntity
import com.vitaltrack.app.data.local.entity.SleepEntity
import com.vitaltrack.app.data.local.entity.StepCounterEntity
import com.vitaltrack.app.data.local.entity.UserEntity
import com.vitaltrack.app.data.local.entity.WaterIntakeEntity

@Database(
    entities = [
        UserEntity::class,
        StepCounterEntity::class,
        GpsTrackingEntity::class,
        WaterIntakeEntity::class,
        SleepEntity::class,
        MealEntity::class
               ],
    version = 6,
    exportSchema = false
)

abstract class VitalTrackDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun stepCounterDao(): StepCounterDao
    abstract fun gpsTrackingDao(): GpsTrackingDao
    abstract fun waterIntakeDao(): WaterIntakeDao
    abstract fun sleepDao(): SleepDao
    abstract fun mealDao(): MealDao
}