package com.example.trialproject3.Map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.content.res.AppCompatResources
import com.example.trialproject3.R
import com.example.trialproject3.databinding.ActivityMapboxMapBinding
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.common.location.compat.permissions.PermissionsListener
import com.mapbox.common.location.compat.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.CameraAnimatorOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location

class MapboxMapActivity : AppCompatActivity(), PermissionsListener {
    private lateinit var binding : ActivityMapboxMapBinding

    private lateinit var permissionsManager: PermissionsManager
    private lateinit var mapboxMap: MapboxMap

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        binding.mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        binding.mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        binding.mapView.gestures.focalPoint = binding.mapView.getMapboxMap().pixelForCoordinate(it)
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapboxMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (PermissionsManager.areLocationPermissionsGranted(this@MapboxMapActivity)) {
            onMapReady()
        } else {
            permissionsManager = PermissionsManager(this@MapboxMapActivity)
            permissionsManager.requestLocationPermissions(this@MapboxMapActivity)
        }

    }

    private fun onMapReady() {
        mapboxMap = binding.mapView.getMapboxMap().apply {
            loadStyleUri(Style.MAPBOX_STREETS) {

                initializeLocationComponent()
//                loadBookingsToMapFromDatabase()

                binding.mapView.camera.apply {
                    val bearing = createBearingAnimator(
                        CameraAnimatorOptions.cameraAnimatorOptions(
                            -45.0
                        )
                    ) {
                        duration = 4000
                        interpolator = AccelerateDecelerateInterpolator()
                    }
                    val zoom = createZoomAnimator(
                        CameraAnimatorOptions.cameraAnimatorOptions(14.0) {
                            startValue(3.0)
                        }
                    ) {
                        duration = 4000
                        interpolator = AccelerateDecelerateInterpolator()
                    }
                    val pitch = createPitchAnimator(
                        CameraAnimatorOptions.cameraAnimatorOptions(55.0) {
                            startValue(0.0)
                        }
                    ) {
                        duration = 4000
                        interpolator = AccelerateDecelerateInterpolator()
                    }
                    playAnimatorsSequentially(zoom)
                }
            }
        }
    }
    private fun initializeLocationComponent() {

        val locationComponentPlugin = binding.mapView.location
        locationComponentPlugin.addOnIndicatorPositionChangedListener {

            //store the current location
//            currentLongitude = it.longitude()
//            currentLatitude = it.latitude()
//
//            DataStorer.storePickupLatitude = currentLatitude
//            DataStorer.storePickupLongitude = currentLongitude
//
//            createViewAnnotation(
//                binding.mapView,
//                Point.fromLngLat(currentLongitude, currentLatitude)
//            )
        }


        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    this@MapboxMapActivity,
                    R.drawable.mapbox_navigation_puck_icon,
                ),
                scaleExpression = Expression.interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        locationComponentPlugin.addOnIndicatorBearingChangedListener(
            onIndicatorBearingChangedListener
        )
    }
    private fun onCameraTrackingDismissed() {
        binding.mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        binding.mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        binding.mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    private fun zoomInCamera(coordinate: Point) {
        binding.zoomInImgBtn.visibility = View.GONE
        binding.zoomOutImgBtn.visibility = View.VISIBLE
        binding.mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(16.0)
                .center(coordinate)
                .build()
        )
    }

    private fun zoomOutCamera(coordinate: Point) {
        binding.zoomInImgBtn.visibility = View.VISIBLE
        binding.zoomOutImgBtn.visibility = View.GONE
        binding.mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(10.0)
                .center(coordinate)
                .build()
        )
    }

    private fun recenterCamera(coordinate: Point) {
        binding.mapView.location
            .addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        binding.mapView.location
            .addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        binding.mapView.gestures.addOnMoveListener(onMoveListener)

        binding.mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(coordinate)
                .build()
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        TODO("Not yet implemented")
    }

    override fun onPermissionResult(granted: Boolean) {
        TODO("Not yet implemented")
    }
}