package com.vitaltrack.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vitaltrack.app.R
import com.vitaltrack.app.data.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class ContextAdjustWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userRepository: UserRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val user = userRepository.getUserOnce() ?: return Result.success()

        var newGoal = user.dailyWaterGoalMl
        var adjusted = false
        var reason = ""

        // verifica acelerômetro
        val sensorManager = applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor != null) {
            val prefs = applicationContext.getSharedPreferences("vital_prefs", Context.MODE_PRIVATE)
            val lastSteps = prefs.getInt("last_steps_check", 0)
            val currentSteps = prefs.getInt("current_steps", 0)
            val diff = currentSteps - lastSteps
            prefs.edit().putInt("last_steps_check", currentSteps).apply()

            // mais de 2000 passos em 20 min = atividade intensa
            if (diff > 2000) {
                newGoal = (newGoal * 1.15f).toInt()
                adjusted = true
                reason += "atividade intensa detectada"
            }
        }

        // verifica GPS — se tem localização recente assume externo
        val prefs = applicationContext.getSharedPreferences("vital_prefs", Context.MODE_PRIVATE)
        val isOutdoor = prefs.getBoolean("is_outdoor", false)
        if (isOutdoor) {
            newGoal = (newGoal * 1.10f).toInt()
            adjusted = true
            reason += if (reason.isNotEmpty()) " e ambiente externo" else "ambiente externo"
        }

        if (adjusted) {
            userRepository.updateUser(user.copy(dailyWaterGoalMl = newGoal))
            sendNotification("Meta de água ajustada para ${newGoal}ml devido a $reason.")
        }

        return Result.success()
    }

    private fun sendNotification(message: String) {
        val channelId = "water_goal_channel"
        val manager = applicationContext.getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Meta de Água",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("VitalTrack — Hidratação")
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .build()

        manager.notify(2, notification)
    }
}