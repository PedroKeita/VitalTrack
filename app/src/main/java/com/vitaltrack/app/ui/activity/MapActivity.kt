package com.vitaltrack.app.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.vitaltrack.app.databinding.ActivityMapBinding
import com.vitaltrack.app.ui.map.MapViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@AndroidEntryPoint
class MapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapBinding
    private val viewModel: MapViewModel by viewModels()
    private lateinit var locationOverlay: MyLocationNewOverlay
    private val polyline = Polyline()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            setupMap()
            viewModel.startTracking()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(
            this,
            PreferenceManager.getDefaultSharedPreferences(this)
        )
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()
        setupButtons()
        observeViewModel()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            setupMap()
            viewModel.startTracking()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun setupMap() {
        binding.mapView.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(17.0)
        }

        locationOverlay = MyLocationNewOverlay(
            GpsMyLocationProvider(this),
            binding.mapView
        )
        locationOverlay.enableMyLocation()
        locationOverlay.enableFollowLocation()
        binding.mapView.overlays.add(locationOverlay)
        binding.mapView.overlays.add(polyline)
    }

    private fun setupButtons() {
        binding.btnFinish.setOnClickListener {
            viewModel.stopTracking()
            finish()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            launch {
                viewModel.points.collect { points ->
                    if (points.isNotEmpty()) {
                        val geoPoints = points.map { GeoPoint(it.latitude, it.longitude) }
                        polyline.setPoints(geoPoints)
                        binding.mapView.invalidate()
                    }
                }
            }
            launch {
                viewModel.distance.collect { distance ->
                    binding.tvDistance.text = "%.0f m".format(distance)
                }
            }
            launch {
                viewModel.speed.collect { speed ->
                    binding.tvSpeed.text = "%.1f km/h".format(speed)
                }
            }
            launch {
                viewModel.duration.collect { duration ->
                    val minutes = duration / 60
                    val seconds = duration % 60
                    binding.tvDuration.text = "%02d:%02d".format(minutes, seconds)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }
}