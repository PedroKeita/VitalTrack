package com.vitaltrack.app.worker

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object HydrationReminderScheduler {

    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
            2, TimeUnit.HOURS
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "hydration_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("hydration_reminder")
    }
}