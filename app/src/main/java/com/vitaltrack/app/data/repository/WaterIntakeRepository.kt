package com.vitaltrack.app.data.repository

import com.vitaltrack.app.data.local.dao.WaterIntakeDao
import com.vitaltrack.app.data.local.entity.WaterIntakeEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WaterIntakeRepository @Inject constructor(
    private val waterIntakeDao: WaterIntakeDao
) {

    private fun today(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private fun now(): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    fun getTodayIntakes(): Flow<List<WaterIntakeEntity>> =
        waterIntakeDao.getByDate(today())

    fun getTodayTotal(): Flow<Int?> =
        waterIntakeDao.getTotalByDate(today())

    suspend fun addWater(amountMl: Int) {
        waterIntakeDao.insert(
            WaterIntakeEntity(
                amountMl = amountMl,
                date = today(),
                time = now()
            )
        )
    }

    suspend fun delete(water: WaterIntakeEntity) {
        waterIntakeDao.delete(water)
    }

    suspend fun getLast(days: Int): List<WaterIntakeDao.WaterDailySummary> =
        waterIntakeDao.getLast(days)
}