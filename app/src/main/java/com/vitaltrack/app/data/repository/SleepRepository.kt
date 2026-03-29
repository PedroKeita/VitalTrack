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

    suspend fun getLastTwo(): List<SleepEntity> = sleepDao.getLastTwo()

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

    fun calculateScore(sleep: SleepEntity, previousSleep: SleepEntity?): Int {
        val durationMinutes = sleep.durationMinutes ?: 0

        // mínimo de 60 minutos para ter pontuação
        if (durationMinutes < 60) return 0

        var score = 0
        val hours = durationMinutes / 60f

        score += when {
            hours >= 7f && hours <= 9f -> 60
            hours >= 6f && hours < 7f  -> 45
            hours >= 9f && hours < 10f -> 45
            hours >= 5f && hours < 6f  -> 30
            hours >= 1f && hours < 5f  -> 15
            else                       -> 0
        }

        if (previousSleep != null) {
            val fmt = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            try {
                val prevStart = fmt.parse(previousSleep.startTime)
                val currStart = fmt.parse(sleep.startTime)
                if (prevStart != null && currStart != null) {
                    val diff = Math.abs(currStart.time - prevStart.time) / 60000
                    score += when {
                        diff <= 30  -> 25
                        diff <= 60  -> 15
                        diff <= 120 -> 10
                        else        -> 0
                    }
                }
            } catch (e: Exception) { score += 10 }
        } else {
            score += 10
        }

        score += when {
            sleep.agitationCount == 0 -> 15
            sleep.agitationCount <= 3 -> 10
            sleep.agitationCount <= 8 -> 5
            else                      -> 0
        }

        return score.coerceIn(0, 100)
    }
    fun getClassification(score: Int): String = when {
        score >= 80 -> "Excelente"
        score >= 60 -> "Bom"
        score >= 40 -> "Regular"
        else        -> "Ruim"
    }

    fun getTip(score: Int): String = when {
        score >= 80 -> "Parabéns! Você está dormindo muito bem. Continue mantendo essa rotina."
        score >= 60 -> "Seu sono está bom. Tente manter horários mais consistentes para melhorar."
        score >= 40 -> "Seu sono está regular. Evite telas antes de dormir e mantenha um horário fixo."
        else        -> "Seu sono precisa de atenção. Tente dormir e acordar sempre no mesmo horário."
    }

    suspend fun delete(sleep: SleepEntity) {
        sleepDao.delete(sleep)
    }


}