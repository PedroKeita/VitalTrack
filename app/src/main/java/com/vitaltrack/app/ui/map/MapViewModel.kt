package com.vitaltrack.app.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.vitaltrack.app.data.repository.GpsTrackingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LatLng(val latitude: Double, val longitude: Double)

@HiltViewModel
class MapViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gpsTrackingRepository: GpsTrackingRepository
) : ViewModel() {

    private val _points = MutableStateFlow<List<LatLng>>(emptyList())
    val points: StateFlow<List<LatLng>> = _points.asStateFlow()

    private val _distance = MutableStateFlow(0f)
    val distance: StateFlow<Float> = _distance.asStateFlow()

    private val _speed = MutableStateFlow(0f)
    val speed: StateFlow<Float> = _speed.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private var isTracking = false
    private var lastLocation: Location? = null
    private var startTime = 0L

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { location ->
                val newPoint = LatLng(location.latitude, location.longitude)
                _points.value = _points.value + newPoint
                _speed.value = location.speed * 3.6f

                lastLocation?.let { last ->
                    val results = FloatArray(1)
                    Location.distanceBetween(
                        last.latitude, last.longitude,
                        location.latitude, location.longitude,
                        results
                    )
                    _distance.value += results[0]
                }
                lastLocation = location
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startTracking() {
        isTracking = true
        startTime = System.currentTimeMillis()

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 2000L
        ).setMinUpdateDistanceMeters(5f).build()

        fusedLocationClient.requestLocationUpdates(
            request, locationCallback, Looper.getMainLooper()
        )

        viewModelScope.launch {
            while (isTracking) {
                delay(1000)
                _duration.value = (System.currentTimeMillis() - startTime) / 1000
            }
        }
    }

    fun stopTracking() {
        isTracking = false
        fusedLocationClient.removeLocationUpdates(locationCallback)

        viewModelScope.launch {
            val durationSeconds = _duration.value
            val avgSpeed = if (durationSeconds > 0)
                (_distance.value / durationSeconds) * 3.6f else 0f

            gpsTrackingRepository.saveTracking(
                durationSeconds = durationSeconds,
                distanceMeters = _distance.value,
                avgSpeedKmh = avgSpeed,
                points = _points.value
            )
        }
    }
}