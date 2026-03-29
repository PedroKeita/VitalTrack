package com.vitaltrack.app.data.repository

import com.vitaltrack.app.data.local.dao.SleepDao
import com.vitaltrack.app.data.local.entity.SleepEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepRepository @Inject constructor(
    private val sleepDao: SleepDao
) {

    private fun today(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private fun now(): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    fun getLatest(): Flow<SleepEntity?> = sleepDao.getLatest()

    fun getLast7Days(): Flow<List<SleepEntity>> = sleepDao.getLast7Days()

    suspend fun getOngoing(): SleepEntity? = sleepDao.getOngoing()

    suspend fun startSleep(): Long {
        val sleep = SleepEntity(
            date = today(),
            startTime = now(),
            endTime = null,
            durationMinutes = null
        )
        return sleepDao.insert(sleep)
    }

    suspend fun startSleepManual(startTime: String): Long {
        val sleep = SleepEntity(
            date = today(),
            startTime = startTime,
            endTime = null,
            durationMinutes = null
        )
        return sleepDao.insert(sleep)
    }

    suspend fun endSleep(sleep: SleepEntity, endTime: String? = null): SleepEntity {
        val end = endTime ?: now()
        val duration = calculateDuration(sleep.startTime, end)
        val updated = sleep.copy(endTime = end, durationMinutes = duration)
        sleepDao.update(updated)
        return updated
    }

    suspend fun incrementAgitation(sleep: SleepEntity) {
        sleepDao.update(sleep.copy(agitationCount = sleep.agitationCount + 1))
    }

    suspend fun saveComplete(sleep: SleepEntity) {
        sleepDao.insert(sleep)
    }

    private fun calculateDuration(start: String, end: String): Int {
        return try {
            val fmt = SimpleDateFormat("HH:mm", Locale.getDefault())
            val startDate = fmt.parse(start) ?: return 0
            val endDate = fmt.parse(end) ?: return 0
            var diff = ((endDate.time - startDate.time) / 60000).toInt()
            if (diff < 0) diff += 24 * 60 // passou da meia-noite
            diff
        } catch (e: Exception) {
            0
        }
    }


}