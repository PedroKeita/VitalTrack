package com.vitaltrack.app.worker

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object ContextAdjustScheduler {

    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<ContextAdjustWorker>(
            20, TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "context_adjust",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("context_adjust")
    }
}