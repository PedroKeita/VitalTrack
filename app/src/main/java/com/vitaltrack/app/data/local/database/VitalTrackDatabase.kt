package com.vitaltrack.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vitaltrack.app.data.local.dao.UserDao
import com.vitaltrack.app.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)

abstract class VitalTrackDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}