package com.vitaltrack.app.data.repository

import com.vitaltrack.app.data.local.dao.GpsTrackingDao
import com.vitaltrack.app.data.local.entity.GpsTrackingEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GpsTrackingRepository @Inject constructor(
    private val gpsTrackingDao: GpsTrackingDao
) {

    fun getAllTrackings(): Flow<List<GpsTrackingEntity>> {
        return gpsTrackingDao.getAllTrackings()
    }

    suspend fun saveTracking(
        durationSeconds: Long,
        distanceMeters: Float,
        avgSpeedKmh: Float,
        points: List<com.vitaltrack.app.ui.map.LatLng>
    ) {
        val polyline = points.joinToString(";") { "${it.latitude},${it.longitude}" }
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

        gpsTrackingDao.insertTracking(
            GpsTrackingEntity(
                date = date,
                durationSeconds = durationSeconds,
                distanceMeters = distanceMeters,
                avgSpeedKmh = avgSpeedKmh,
                polylinePoints = polyline
            )
        )
    }
}