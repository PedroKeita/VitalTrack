package com.vitaltrack.app.ui.sleep

import com.vitaltrack.app.data.local.entity.SleepEntity

data class SleepWithScore(
    val sleep: SleepEntity,
    val score: Int,
    val classification: String
)