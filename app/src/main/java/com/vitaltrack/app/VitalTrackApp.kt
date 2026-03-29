package com.vitaltrack.app

import android.app.Application
import com.vitaltrack.app.worker.ContextAdjustScheduler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VitalTrackApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ContextAdjustScheduler.schedule(this)
    }
}