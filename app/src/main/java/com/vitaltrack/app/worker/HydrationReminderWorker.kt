package com.vitaltrack.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vitaltrack.app.R
import com.vitaltrack.app.data.repository.UserRepository
import com.vitaltrack.app.data.repository.WaterIntakeRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Calendar

@HiltWorker
class HydrationReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userRepository: UserRepository,
    private val waterIntakeRepository: WaterIntakeRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val prefs = applicationContext.getSharedPreferences("vital_prefs", Context.MODE_PRIVATE)

        // verifica se lembretes estão ativados
        val remindersEnabled = prefs.getBoolean("reminders_enabled", true)
        if (!remindersEnabled) return Result.success()

        val user = userRepository.getUserOnce() ?: return Result.success()

        // verifica horário de sono
        val sleepHour = prefs.getInt("sleep_hour", 22)
        val wakeHour = prefs.getInt("wake_hour", 7)
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val isSleepTime = if (sleepHour > wakeHour) {
            currentHour >= sleepHour || currentHour < wakeHour
        } else {
            currentHour in sleepHour until wakeHour
        }

        if (isSleepTime) return Result.success()

        // verifica progresso
        val totalMl = waterIntakeRepository.getTodayTotal().first() ?: 0
        val goalMl = user.dailyWaterGoalMl
        val hoursAwake = currentHour - wakeHour
        val expectedMl = if (hoursAwake > 0) (goalMl / 16f * hoursAwake).toInt() else 0

        // só notifica se estiver abaixo do esperado
        if (totalMl >= expectedMl) return Result.success()

        val remaining = goalMl - totalMl
        sendNotification(remaining)

        return Result.success()
    }

    private fun sendNotification(remainingMl: Int) {
        val channelId = "hydration_reminder_channel"
        val manager = applicationContext.getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Lembretes de Hidratação",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("💧 Hora de beber água!")
            .setContentText("Faltam ${remainingMl}ml para atingir sua meta de hoje.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .build()

        manager.notify(3, notification)
    }
}