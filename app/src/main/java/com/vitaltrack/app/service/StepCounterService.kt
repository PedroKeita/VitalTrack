package com.vitaltrack.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.vitaltrack.app.R
import com.vitaltrack.app.data.repository.StepCounterRepository
import com.vitaltrack.app.data.repository.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class StepCounterService : Service(), SensorEventListener {

    @Inject lateinit var stepCounterRepository: StepCounterRepository
    @Inject lateinit var userRepository: UserRepository

    private lateinit var sensorManager: SensorManager
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private var initialSteps = -1
    private var currentSteps = 0
    private var userWeight = 70f
    private var lastDate = today()

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        loadUserWeight()
        startForeground(1, buildNotification(0))
        registerStepSensor()
    }

    private fun loadUserWeight() {
        scope.launch {
            userRepository.getUserOnce()?.let {
                userWeight = it.weight
                currentSteps = stepCounterRepository.getTodayStepsOnce() ?: 0
            }
        }
    }

    private fun registerStepSensor() {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0].toInt()
            val currentDate = today()

            if (currentDate != lastDate) {
                initialSteps = totalSteps
                lastDate = currentDate
                currentSteps = 0
            }

            if (initialSteps == -1) {
                initialSteps = totalSteps - currentSteps
            }

            currentSteps = totalSteps - initialSteps

            scope.launch {
                stepCounterRepository.saveSteps(currentSteps, userWeight)
            }

            updateNotification(currentSteps)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun today(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun buildNotification(steps: Int): Notification {
        val channelId = "step_counter_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Contador de Passos",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("VitalTrack")
            .setContentText("$steps passos hoje")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(steps: Int) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(1, buildNotification(steps))
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        job.cancel()
    }
}