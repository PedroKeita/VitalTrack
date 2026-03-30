package com.vitaltrack.app.data.repository

import com.vitaltrack.app.data.local.dao.StepCounterDao
import com.vitaltrack.app.data.local.entity.StepCounterEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StepCounterRepository @Inject constructor(
    private val stepCounterDao: StepCounterDao
) {

    private fun today(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun getTodaySteps(): Flow<StepCounterEntity?> {
        return stepCounterDao.getStepsByDate(today())
    }

    suspend fun saveSteps(steps: Int, weightKg: Float) {
        val calories = steps * 0.04f * weightKg / 70f
        stepCounterDao.insertOrUpdate(
            StepCounterEntity(
                date = today(),
                steps = steps,
                calories = calories
            )
        )
    }

    suspend fun getTodayStepsOnce(): Int? {
        return stepCounterDao.getStepsByDateOnce(today())
    }

    suspend fun getLast(days: Int): List<StepCounterEntity> =
        stepCounterDao.getLast(days)
}