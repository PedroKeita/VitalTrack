package com.vitaltrack.app.di

import android.content.Context
import androidx.room.Room
import com.vitaltrack.app.data.local.dao.GpsTrackingDao
import com.vitaltrack.app.data.local.dao.StepCounterDao
import com.vitaltrack.app.data.local.database.VitalTrackDatabase
import com.vitaltrack.app.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun proveideDatabase(@ApplicationContext context: Context): VitalTrackDatabase {
        return Room.databaseBuilder(
            context,
            VitalTrackDatabase::class.java,
            "vitaltrack.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(database: VitalTrackDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideStepCounterDao(database: VitalTrackDatabase): StepCounterDao {
        return database.stepCounterDao()
    }

    @Provides
    fun provideGpsTrackingDao(database: VitalTrackDatabase): GpsTrackingDao {
        return database.gpsTrackingDao()
    }
}