package com.vitaltrack.app

import android.app.Application
import com.vitaltrack.app.worker.ContextAdjustScheduler
import dagger.hilt.android.HiltAndroidApp
import com.vitaltrack.app.worker.HydrationReminderScheduler

@HiltAndroidApp
class VitalTrackApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ContextAdjustScheduler.schedule(this)
        HydrationReminderScheduler.schedule(this)
    }
}