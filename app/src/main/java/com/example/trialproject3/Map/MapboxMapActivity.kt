package com.example.trialproject3.Map

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.example.trialproject3.Activity.MainActivity
import com.example.trialproject3.BottomSheetModal.RegisterSellerStoreBottomSheetFragment
import com.example.trialproject3.BottomSheetModal.ShowStoreInfoBottomSheetFragment
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Helper.AlertDialogHelper
import com.example.trialproject3.R
import com.example.trialproject3.databinding.ActivityMapboxMapBinding
import com.example.trialproject3.databinding.MapboxItemViewAnnotationBinding
import com.mapbox.api.directions.v5.models.Bearing
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.bindgen.Expected
import com.mapbox.common.location.compat.permissions.PermissionsListener
import com.mapbox.common.location.compat.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.CameraAnimatorOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.mapbox.navigation.base.TimeFormat
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import com.mapbox.navigation.base.formatter.DistanceFormatterOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.NavigationRouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.formatter.MapboxDistanceFormatter
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.core.lifecycle.MapboxNavigationObserver
import com.mapbox.navigation.core.lifecycle.requireMapboxNavigation
import com.mapbox.navigation.core.replay.MapboxReplayer
import com.mapbox.navigation.core.replay.ReplayLocationEngine
import com.mapbox.navigation.core.replay.route.ReplayProgressObserver
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.core.trip.session.VoiceInstructionsObserver
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer
import com.mapbox.navigation.ui.maneuver.api.MapboxManeuverApi
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.navigation.ui.maps.camera.lifecycle.NavigationBasicGesturesHandler
import com.mapbox.navigation.ui.maps.camera.state.NavigationCameraState
import com.mapbox.navigation.ui.maps.camera.transition.NavigationCameraTransitionOptions
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.arrow.model.RouteArrowOptions
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions
import com.mapbox.navigation.ui.tripprogress.api.MapboxTripProgressApi
import com.mapbox.navigation.ui.tripprogress.model.DistanceRemainingFormatter
import com.mapbox.navigation.ui.tripprogress.model.EstimatedTimeToArrivalFormatter
import com.mapbox.navigation.ui.tripprogress.model.PercentDistanceTraveledFormatter
import com.mapbox.navigation.ui.tripprogress.model.TimeRemainingFormatter
import com.mapbox.navigation.ui.tripprogress.model.TripProgressUpdateFormatter
import com.mapbox.navigation.ui.voice.api.MapboxSpeechApi
import com.mapbox.navigation.ui.voice.api.MapboxVoiceInstructionsPlayer
import com.mapbox.navigation.ui.voice.model.SpeechAnnouncement
import com.mapbox.navigation.ui.voice.model.SpeechError
import com.mapbox.navigation.ui.voice.model.SpeechValue
import com.mapbox.navigation.ui.voice.model.SpeechVolume
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class MapboxMapActivity : AppCompatActivity(), PermissionsListener,
    ShowStoreInfoBottomSheetFragment.OnNavigateListener {
    private val TAG = "MapboxMapActivity"
    private lateinit var binding: ActivityMapboxMapBinding
    private lateinit var alertDialogHelper: AlertDialogHelper

    private lateinit var permissionsManager: PermissionsManager
    private val LOCATION_PERMISSION_REQUEST_CODE = 123
    private val REQUEST_ENABLE_LOCATION = 1
    private var isNavigatingToDestination = false
    private var isRenavigating = false
    private var isAcceptedABooking = false
    private var lastBackButtonPressTime: Long = 0
    private var pointAnnotation: PointAnnotation? = null
    private var lastInteractedAnnotationOptions: PointAnnotationOptions? = null
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private lateinit var storeAnnotationManager: PointAnnotationManager
    private lateinit var storedestinationPointAnnotation: PointAnnotation
    private val pointAnnotationList: MutableList<PointAnnotation> = mutableListOf()

    //navigation
    private val mapboxReplayer = MapboxReplayer()
    private val replayLocationEngine = ReplayLocationEngine(mapboxReplayer)
    private val replayProgressObserver = ReplayProgressObserver(mapboxReplayer)
    private lateinit var navigationCamera: NavigationCamera
    private lateinit var viewportDataSource: MapboxNavigationViewportDataSource
    private val pixelDensity = Resources.getSystem().displayMetrics.density
    private val overviewPadding: EdgeInsets by lazy {
        EdgeInsets(
            140.0 * pixelDensity,
            40.0 * pixelDensity,
            120.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }
    private val landscapeOverviewPadding: EdgeInsets by lazy {
        EdgeInsets(
            30.0 * pixelDensity,
            380.0 * pixelDensity,
            110.0 * pixelDensity,
            20.0 * pixelDensity
        )
    }
    private val followingPadding: EdgeInsets by lazy {
        EdgeInsets(
            180.0 * pixelDensity,
            40.0 * pixelDensity,
            150.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }
    private val landscapeFollowingPadding: EdgeInsets by lazy {
        EdgeInsets(
            30.0 * pixelDensity,
            380.0 * pixelDensity,
            110.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }
    private lateinit var maneuverApi: MapboxManeuverApi
    private lateinit var tripProgressApi: MapboxTripProgressApi
    private lateinit var routeLineApi: MapboxRouteLineApi
    private lateinit var routeLineView: MapboxRouteLineView
    private val routeArrowApi: MapboxRouteArrowApi = MapboxRouteArrowApi()
    private lateinit var routeArrowView: MapboxRouteArrowView
    private var isVoiceInstructionsMuted = false
        set(value) {
            field = value
            if (value) {
                binding.soundBtn.muteAndExtend(BUTTON_ANIMATION_DURATION)
                voiceInstructionsPlayer.volume(SpeechVolume(0f))
            } else {
                binding.soundBtn.unmuteAndExtend(BUTTON_ANIMATION_DURATION)
                voiceInstructionsPlayer.volume(SpeechVolume(1f))
            }
        }

    private lateinit var speechApi: MapboxSpeechApi

    private lateinit var voiceInstructionsPlayer: MapboxVoiceInstructionsPlayer

    private val voiceInstructionsObserver = VoiceInstructionsObserver { voiceInstructions ->
        speechApi.generate(voiceInstructions, speechCallback)
    }

    private val speechCallback =
        MapboxNavigationConsumer<Expected<SpeechError, SpeechValue>> { expected ->
            expected.fold(
                { error ->
                    // play the instruction via fallback text-to-speech engine
                    voiceInstructionsPlayer.play(
                        error.fallback,
                        voiceInstructionsPlayerCallback
                    )
                },
                { value ->
                    // play the sound file from the external generator
                    voiceInstructionsPlayer.play(
                        value.announcement,
                        voiceInstructionsPlayerCallback
                    )
                }
            )
        }

    private val voiceInstructionsPlayerCallback =
        MapboxNavigationConsumer<SpeechAnnouncement> { value ->
            // remove already consumed file to free-up space
            speechApi.clean(value)
        }

    private val navigationLocationProvider = NavigationLocationProvider()
    private val locationObserver = object : LocationObserver {
        var firstLocationUpdateReceived = false

        override fun onNewRawLocation(rawLocation: Location) {
            // not handled
        }

        override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
            val enhancedLocation = locationMatcherResult.enhancedLocation

            createViewAnnotation(
                binding.mapView,
                Point.fromLngLat(enhancedLocation.longitude, enhancedLocation.latitude)
            )

            // update location puck's position on the map
            navigationLocationProvider.changePosition(
                location = enhancedLocation,
                keyPoints = locationMatcherResult.keyPoints,
            )

            // update camera position to account for new location
            viewportDataSource.onLocationChanged(enhancedLocation)
            viewportDataSource.evaluate()

            // if this is the first location update the activity has received,
            // it's best to immediately move the camera to the current user location
            if (!firstLocationUpdateReceived) {
                firstLocationUpdateReceived = true
                navigationCamera.requestNavigationCameraToOverview(
                    stateTransitionOptions = NavigationCameraTransitionOptions.Builder()
                        .maxDuration(0) // instant transition
                        .build()
                )
            }
        }
    }

    private val routeProgressObserver = RouteProgressObserver { routeProgress ->

        val DISTANCE_THRESHOLD = 50.0

        val remainingDistance = routeProgress.distanceRemaining

        if (remainingDistance <= DISTANCE_THRESHOLD) {
            // Driver has arrived at the destination
        }

        // update the camera position to account for the progressed fragment of the route
        viewportDataSource.onRouteProgressChanged(routeProgress)
        viewportDataSource.evaluate()

        // draw the upcoming maneuver arrow on the map
        val style = binding.mapView.getMapboxMap().getStyle()
        if (style != null) {
            val maneuverArrowResult = routeArrowApi.addUpcomingManeuverArrow(routeProgress)
            routeArrowView.renderManeuverUpdate(style, maneuverArrowResult)
        }

        // update top banner with maneuver instructions
        val maneuvers = maneuverApi.getManeuvers(routeProgress)
        maneuvers.fold(
            { error ->
                Toast.makeText(
                    this@MapboxMapActivity,
                    error.errorMessage,
                    Toast.LENGTH_SHORT
                ).show()
            },
            {
                binding.maneuverView.visibility = View.VISIBLE
                binding.maneuverView.renderManeuvers(maneuvers)
            }
        )

        // update bottom trip progress summary
        binding.tripProgressView.render(
            tripProgressApi.getTripProgress(routeProgress)
        )
    }

    private val routesObserver = RoutesObserver { routeUpdateResult ->
        if (routeUpdateResult.navigationRoutes.isNotEmpty()) {
            // generate route geometries asynchronously and render them
            routeLineApi.setNavigationRoutes(
                routeUpdateResult.navigationRoutes
            ) { value ->
                binding.mapView.getMapboxMap().getStyle()?.apply {
                    routeLineView.renderRouteDrawData(this, value)
                }
            }

            // update the camera position to account for the new route
            viewportDataSource.onRouteChanged(routeUpdateResult.navigationRoutes.first())
            viewportDataSource.evaluate()
        } else {
            // remove the route line and route arrow from the map
            val style = binding.mapView.getMapboxMap().getStyle()
            if (style != null) {
                routeLineApi.clearRouteLine { value ->
                    routeLineView.renderClearRouteLineValue(
                        style,
                        value
                    )
                }
                routeArrowView.render(style, routeArrowApi.clearArrows())
            }

            // remove the route reference from camera position evaluations
            viewportDataSource.clearRouteData()
            viewportDataSource.evaluate()
        }
    }

    private val mapboxNavigation: MapboxNavigation by requireMapboxNavigation(
        onResumedObserver = object : MapboxNavigationObserver {
            @SuppressLint("MissingPermission")
            override fun onAttached(mapboxNavigation: MapboxNavigation) {
                mapboxNavigation.registerRoutesObserver(routesObserver)
                mapboxNavigation.registerLocationObserver(locationObserver)
                mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
                mapboxNavigation.registerRouteProgressObserver(replayProgressObserver)
                mapboxNavigation.registerVoiceInstructionsObserver(voiceInstructionsObserver)
                // start the trip session to being receiving location updates in free drive
                // and later when a route is set also receiving route progress updates
                mapboxNavigation.startTripSession()
            }

            override fun onDetached(mapboxNavigation: MapboxNavigation) {
                mapboxNavigation.unregisterRoutesObserver(routesObserver)
                mapboxNavigation.unregisterLocationObserver(locationObserver)
                mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
                mapboxNavigation.unregisterRouteProgressObserver(replayProgressObserver)
                mapboxNavigation.unregisterVoiceInstructionsObserver(voiceInstructionsObserver)
            }
        },
        onInitialize = this::initializeNavigationComponents
    )

    private val viewAnnotationMap = mutableMapOf<Point, View>()

    interface OnNavigateListener {
        fun onNavigateClick(longitude: Double, latitude: Double)
    }

    companion object {
        var currentLatitude: Double = 0.0
        var currentLongitude: Double = 0.0
        private const val BUTTON_ANIMATION_DURATION = 1500L
        private const val DOUBLE_BACK_PRESS_INTERVAL = 2000 // 2 seconds

    }

    override fun onDestroy() {
        super.onDestroy()

        mapboxReplayer.finish()
        maneuverApi.cancel()
        routeLineApi.cancel()
        routeLineView.cancel()
        speechApi.cancel()
        voiceInstructionsPlayer.shutdown()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapboxMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alertDialogHelper = AlertDialogHelper(this@MapboxMapActivity)

        checkLocationPermission()
        loadStoresLocationInMap()

        binding.tripProgressLayout.visibility = View.INVISIBLE
        binding.soundBtn.visibility = View.INVISIBLE
        binding.maneuverView.visibility = View.INVISIBLE
        binding.navigationStatusTextView.visibility = View.GONE
        binding.pingLocationImgBtn.visibility = View.GONE
        binding.pingLocationImgBtn2.visibility = View.GONE
        binding.showTripProgressImgBtn.visibility = View.GONE

//        binding.chatImgBtn.setOnClickListener {
//            intent = Intent(this@MapboxMapActivity, ChatOverviewActivity::class.java)
//            startActivity(intent)
//        }

        binding.mapStyleSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.mapView.getMapboxMap().apply {
                    loadStyleUri(Style.SATELLITE_STREETS) {

                        showToast("Changed Map style to Streets")

                    }
                }
            } else {
                binding.mapView.getMapboxMap().apply {
                    loadStyleUri(Style.TRAFFIC_DAY) {

                        showToast("Changed Map style to Satellite")

                    }
                }
            }
        }

        binding.fullscreenImgBtn.setOnClickListener {
            Toast.makeText(
                this@MapboxMapActivity,
                "Entered Fullscreen", Toast.LENGTH_SHORT
            ).show()

            binding.fullscreenImgBtn.visibility = View.GONE
            binding.minimizeScreenImgBtn.visibility = View.VISIBLE

//            binding.bottomNavigationView.visibility = View.GONE
        }

        binding.minimizeScreenImgBtn.setOnClickListener {
            Toast.makeText(
                this@MapboxMapActivity,
                "Exited Fullscreen", Toast.LENGTH_SHORT
            ).show()

            binding.minimizeScreenImgBtn.visibility = View.GONE
            binding.fullscreenImgBtn.visibility = View.VISIBLE

//            binding.bottomNavigationView.visibility = View.VISIBLE
        }

        binding.stopNavigationImgBtn.setOnClickListener {
            clearRouteAndStopNavigation()
        }
        binding.recenterBtn.setOnClickListener {
            navigationCamera.requestNavigationCameraToFollowing()
            binding.routeOverviewBtn.showTextAndExtend(BUTTON_ANIMATION_DURATION)
        }
        binding.routeOverviewBtn.setOnClickListener {
            navigationCamera.requestNavigationCameraToOverview()
            binding.recenterBtn.showTextAndExtend(BUTTON_ANIMATION_DURATION)
        }
        binding.soundBtn.setOnClickListener {
            // mute/unmute voice instructions
            isVoiceInstructionsMuted = !isVoiceInstructionsMuted
        }

        // set initial sounds button state
        binding.soundBtn.unmute()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentTime = System.currentTimeMillis()

                if (currentTime - lastBackButtonPressTime < DOUBLE_BACK_PRESS_INTERVAL) {
                    val intent = Intent(this@MapboxMapActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    showToast("Press back again to exit")
                    lastBackButtonPressTime = currentTime
                }

            }
        })
    }


    private fun checkLocationPermission() {
        if (PermissionsManager.areLocationPermissionsGranted(this@MapboxMapActivity)) {
            checkLocationService()
        } else {
            permissionsManager = PermissionsManager(this@MapboxMapActivity)
            permissionsManager.requestLocationPermissions(this@MapboxMapActivity)
        }

    }

    private fun checkLocationService() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (!isGpsEnabled && !isNetworkEnabled) {
            alertDialogHelper.showAlertDialog(
                "Location Service",
                "Please enable location service",
                "Enable",
                { dialog, id ->
                    val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                },
                null,
                null
            )
        } else
            onMapReady()

    }

    @SuppressLint("SetTextI18n")
    private fun onMapReady() {
        binding.mapView.getMapboxMap().apply {
            loadStyleUri(Style.TRAFFIC_DAY) {

                setupGesturesListener()
                initializeLocationComponent()
                initializeNavigationComponents()

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
//        binding.mapView.getMapboxMap().setCamera(
//            CameraOptions.Builder()
//                .zoom(14.0)
//                .build()
//        )
    }

    private fun zoomInCamera(coordinate: Point) {
        binding.zoomInImgBtn.visibility = View.GONE
        binding.zoomOutImgBtn.visibility = View.VISIBLE
        binding.mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(13.0)
                .center(coordinate)
                .build()
        )
    }

    private fun zoomOutCamera(coordinate: Point) {
        binding.zoomInImgBtn.visibility = View.VISIBLE
        binding.zoomOutImgBtn.visibility = View.GONE
        binding.mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(9.0)
                .center(coordinate)
                .build()
        )
    }

    private fun initializeNavigationComponents() {
        // initialize Navigation Camera
        viewportDataSource = MapboxNavigationViewportDataSource(binding.mapView.getMapboxMap())
        navigationCamera = NavigationCamera(
            binding.mapView.getMapboxMap(),
            binding.mapView.camera,
            viewportDataSource
        )
        // set the animations lifecycle listener to ensure the NavigationCamera stops
        // automatically following the user location when the map is interacted with
        binding.mapView.camera.addCameraAnimationsLifecycleListener(
            NavigationBasicGesturesHandler(navigationCamera)
        )
        navigationCamera.registerNavigationCameraStateChangeObserver { navigationCameraState ->
            // shows/hide the recenter button depending on the camera state
            when (navigationCameraState) {
                NavigationCameraState.TRANSITION_TO_FOLLOWING,
                NavigationCameraState.FOLLOWING -> binding.recenterBtn.visibility = View.INVISIBLE

                NavigationCameraState.TRANSITION_TO_OVERVIEW,
                NavigationCameraState.OVERVIEW,
                NavigationCameraState.IDLE -> binding.recenterBtn.visibility = View.VISIBLE
            }
        }
        // set the padding values depending on screen orientation and visible view layout
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewportDataSource.overviewPadding = landscapeOverviewPadding
        } else {
            viewportDataSource.overviewPadding = overviewPadding
        }
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewportDataSource.followingPadding = landscapeFollowingPadding
        } else {
            viewportDataSource.followingPadding = followingPadding
        }

        // make sure to use the same DistanceFormatterOptions across different features
        val distanceFormatterOptions = DistanceFormatterOptions.Builder(this).build()

        // initialize maneuver api that feeds the data to the top banner maneuver view
        maneuverApi = MapboxManeuverApi(
            MapboxDistanceFormatter(distanceFormatterOptions)
        )

        // initialize bottom progress view
        tripProgressApi = MapboxTripProgressApi(
            TripProgressUpdateFormatter.Builder(this)
                .distanceRemainingFormatter(
                    DistanceRemainingFormatter(distanceFormatterOptions)
                )
                .timeRemainingFormatter(
                    TimeRemainingFormatter(this)
                )
                .percentRouteTraveledFormatter(
                    PercentDistanceTraveledFormatter()
                )
                .estimatedTimeToArrivalFormatter(
                    EstimatedTimeToArrivalFormatter(this, TimeFormat.NONE_SPECIFIED)
                )
                .build()
        )

        // initialize voice instructions api and the voice instruction player
        speechApi = MapboxSpeechApi(
            this,
            getString(R.string.mapbox_access_token),
            Locale.US.language
        )
        voiceInstructionsPlayer = MapboxVoiceInstructionsPlayer(
            this,
            getString(R.string.mapbox_access_token),
            Locale.US.language
        )

        // initialize route line, the withRouteLineBelowLayerId is specified to place
        // the route line below road labels layer on the map
        // the value of this option will depend on the style that you are using
        // and under which layer the route line should be placed on the map layers stack
        val mapboxRouteLineOptions = MapboxRouteLineOptions.Builder(this)
            .withRouteLineBelowLayerId("road-label-navigation")
            .build()
        routeLineApi = MapboxRouteLineApi(mapboxRouteLineOptions)
        routeLineView = MapboxRouteLineView(mapboxRouteLineOptions)

        // initialize maneuver arrow view to draw arrows on the map
        val routeArrowOptions = RouteArrowOptions.Builder(this).build()
        routeArrowView = MapboxRouteArrowView(routeArrowOptions)

    }

    private fun initializeLocationComponent() {

        MapboxNavigationApp.setup(
            NavigationOptions.Builder(this)
                .accessToken(getString(R.string.mapbox_access_token))
                // comment out the location engine setting block to disable simulation
//                .locationEngine(replayLocationEngine)
                .build()
        )

        // initialize location puck
        binding.mapView.location.apply {
            setLocationProvider(navigationLocationProvider)
            this.locationPuck = LocationPuck2D(
                bearingImage = ContextCompat.getDrawable(
                    this@MapboxMapActivity,
                    R.drawable.mapbox_navigation_puck_icon
                )
            )
            enabled = true
        }
    }

//    private fun initializeBottomNavButtons(bookingID: String) {
//        binding.bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
//            when (item.itemId) {
//                R.id.trips -> {
//                    intent = Intent(this, TripHistoryActivity::class.java)
//                    startActivity(intent)
//                }
//
//                R.id.bookings -> {
//                    showPassengerBookingsBottomSheet(bookingID)
//                }
//
//                R.id.help -> {
//                    intent = Intent(this, HelpActivity::class.java)
//                    startActivity(intent)
//                }
//            }
//
//            true
//        }
//    }

    private fun setupGesturesListener() {
//        binding.mapView.gestures.addOnMoveListener(onMoveListener)

        binding.mapView.gestures.addOnMapLongClickListener {
//            findRoute(it)
            showRegisterSellerStoreBottomSheet(it)
            true
        }

//        binding.mapView.gestures.addOnMapClickListener {
//            findRoute(it)
//            true
//        }
//        pointAnnotationManager?.apply {
//            addClickListener(
//                OnPointAnnotationClickListener {
//                    Toast.makeText(this@MapPassengerActivity, "id: ${it.id}", Toast.LENGTH_LONG).show()
//                    false
//                }
//            )
//        }
    }

    override fun onNavigateClick(longitude: Double, latitude: Double) {
        val point = Point.fromLngLat(longitude, latitude)
        findRoute(point)
    }

    private fun removeDestinationAnnotationFromMap() {
        pointAnnotation?.let { pointAnnotationManager.delete(it) }
    }

    private fun removeAllAnnotationsExceptLastInteracted() {
        val lastInteracted = lastInteractedAnnotationOptions
        pointAnnotationManager.deleteAll()
        lastInteracted?.let { pointAnnotationManager.create(it) }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap {

        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            val constantState = sourceDrawable?.constantState
            val drawable = constantState?.newDrawable()?.mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable!!.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    private fun loadStoresLocationInMap() {
        val storeReference = FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_STORES)

        storeReference.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val storeDocuments = task.result?.documents
                    if (storeDocuments != null) {
                        for (storeSnapshot in storeDocuments) {
                            val storeData = storeSnapshot.data
                            val longitude = storeData?.get("storeLongitude") as? Double
                            val latitude = storeData?.get("storeLatitude") as? Double
                            val storeID = storeData?.get("storeID") as? String

                            if (storeID != null && longitude != null && latitude != null) {
                                addStoreAnnotationToMap(storeID, longitude, latitude)
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "loadStoresLocationInMap: " + task.exception)
                }
            }
    }

    //display the pin in the map
    private fun addStoreAnnotationToMap(
        storeID: String,
        longitude: Double,
        latitude: Double,
    ) {

        bitmapFromDrawableRes(
            this@MapboxMapActivity,
            R.drawable.store_100
        ).let {
            val annotationApi = binding.mapView.annotations
            storeAnnotationManager = annotationApi.createPointAnnotationManager()
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(longitude, latitude))
                .withIconImage(it)

            storedestinationPointAnnotation = storeAnnotationManager.create(pointAnnotationOptions)
            pointAnnotationList.add(storedestinationPointAnnotation)

            storeAnnotationManager.apply {
                addClickListener(
                    OnPointAnnotationClickListener {

                        showStoreInfoBottomSheet(storeID, longitude, latitude)

                        true
                    }
                )
            }
        }
    }

    private fun onCameraTrackingDismissed() {
//        Toast.makeText(this, "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()

//        binding.mapView.location
//            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
//        binding.mapView.location
//            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
//        binding.mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    private fun showStoreInfoBottomSheet(
        storeID: String,
        longitude: Double,
        latitude: Double
    ) {
        val showStoreInfoBottomSheetFragment =
            ShowStoreInfoBottomSheetFragment.newInstance(storeID, longitude, latitude)
        showStoreInfoBottomSheetFragment.show(
            supportFragmentManager,
            ShowStoreInfoBottomSheetFragment.TAG
        )
    }

    //displays the the "you"
    private fun createViewAnnotation(mapView: MapView, coordinate: Point) {

        //store the current location
        currentLatitude = coordinate.latitude()
        currentLongitude = coordinate.longitude()

        binding.zoomInImgBtn.setOnClickListener {
            zoomInCamera(coordinate)
        }

        binding.zoomOutImgBtn.setOnClickListener {
            zoomOutCamera(coordinate)
        }

        if (viewAnnotationMap[coordinate] == null) {
            mapView.viewAnnotationManager.removeAllViewAnnotations()
            val viewAnnotation = mapView.viewAnnotationManager.addViewAnnotation(
                resId = R.layout.mapbox_item_view_annotation,
                options = viewAnnotationOptions {
                    geometry(coordinate)
                    offsetY(170)
                },
            ).also { view ->
                viewAnnotationMap[coordinate] = view
            }
//            val locationText = """
//                My Location:
//                Longitude = ${coordinate.longitude()}
//                Latitude = ${coordinate.latitude()}
//            """.trimIndent()
//
            MapboxItemViewAnnotationBinding.bind(viewAnnotation).apply {
                annotationBackground.clipToOutline = true
            }
        }
    }


    private fun findRoute(destination: Point) {

        binding.hideTripProgressImgBtn.setOnClickListener {
            binding.tripProgressLayout.visibility = View.GONE
            binding.showTripProgressImgBtn.visibility = View.VISIBLE
            binding.pingLocationImgBtn2.visibility = View.VISIBLE
        }
        binding.showTripProgressImgBtn.setOnClickListener {
            binding.tripProgressLayout.visibility = View.VISIBLE
            binding.showTripProgressImgBtn.visibility = View.GONE
            binding.pingLocationImgBtn2.visibility = View.GONE
        }
        binding.mapView.getMapboxMap().apply {
            loadStyleUri(Style.TRAFFIC_DAY) {
            }
        }

        val originLocation = navigationLocationProvider.lastLocation
        val originPoint = originLocation?.let {
            Point.fromLngLat(it.longitude, it.latitude)
        } ?: return

        // execute a route request
        // it's recommended to use the
        // applyDefaultNavigationOptions and applyLanguageAndVoiceUnitOptions
        // that make sure the route request is optimized
        // to allow for support of all of the Navigation SDK features
        mapboxNavigation.requestRoutes(
            RouteOptions.builder()
                .applyDefaultNavigationOptions()
                .applyLanguageAndVoiceUnitOptions(this)
                .coordinatesList(listOf(originPoint, destination))
                // provide the bearing for the origin of the request to ensure
                // that the returned route faces in the direction of the current user movement
                .bearingsList(
                    listOf(
                        Bearing.builder()
                            .angle(originLocation.bearing.toDouble())
                            .degrees(45.0)
                            .build(),
                        null
                    )
                )
                .layersList(listOf(mapboxNavigation.getZLevel(), null))
                .build(),
            object : NavigationRouterCallback {
                override fun onCanceled(routeOptions: RouteOptions, routerOrigin: RouterOrigin) {
                    Log.i(TAG, routeOptions.toString())
                }

                override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
                    Log.e(TAG, reasons.toString())
                }

                override fun onRoutesReady(
                    routes: List<NavigationRoute>,
                    routerOrigin: RouterOrigin
                ) {
                    setRouteAndStartNavigation(routes)
                }
            }
        )
    }

    private fun setRouteAndStartNavigation(routes: List<NavigationRoute>) {
        // set routes, where the first route in the list is the primary route that
        // will be used for active guidance
        mapboxNavigation.setNavigationRoutes(routes)

        // show UI elements
        binding.soundBtn.visibility = View.VISIBLE
        binding.routeOverviewBtn.visibility = View.VISIBLE
        binding.tripProgressLayout.visibility = View.VISIBLE

//        binding.bottomNavigationView.visibility = View.GONE
        binding.fullscreenImgBtn.visibility = View.GONE
        binding.minimizeScreenImgBtn.visibility = View.GONE

        // move the camera to overview when new route is available
        navigationCamera.requestNavigationCameraToOverview()
    }

    private fun clearRouteAndStopNavigation() {

        showToast("Cancelled Navigation")

        // clear
        mapboxNavigation.setNavigationRoutes(listOf())

        binding.mapView.getMapboxMap().apply {
            loadStyleUri(Style.TRAFFIC_DAY) {
            }
        }

        // stop simulation
        mapboxReplayer.stop()

        // hide UI elements
        binding.soundBtn.visibility = View.INVISIBLE
        binding.maneuverView.visibility = View.INVISIBLE
        binding.routeOverviewBtn.visibility = View.INVISIBLE
        binding.tripProgressLayout.visibility = View.INVISIBLE

//        binding.bottomNavigationView.visibility = View.VISIBLE
        binding.fullscreenImgBtn.visibility = View.VISIBLE
    }

    private fun getCurrentTimeAndDate(): String {
        val calendar = Calendar.getInstance() // Get a Calendar instance
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Months are 0-based, so add 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        return "$month-$day-$year $hour:$minute:$second"
    }


    private fun generateRandomTripID(): String {
        val uuid = UUID.randomUUID()
        return uuid.toString()
    }

    //start the trip when the passenger is on board

    //    private fun showEnableLocationServiceDialog() {
//        builder = AlertDialog.Builder(this)
//        val binding: DialogEnableLocationServiceBinding =
//            DialogEnableLocationServiceBinding.inflate(layoutInflater)
//        val dialogView: View = binding.root
//
//        binding.enableLocationServiceBtn.setOnClickListener { v ->
//            intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//            startActivityForResult(intent, REQUEST_ENABLE_LOCATION)
//            closeEnableLocationServiceDialog()
//        }
//
//        builder.setView(dialogView)
//        enableLocationServiceDialog = builder.create()
//        enableLocationServiceDialog.show()
//    }
//
//    private fun closeEnableLocationServiceDialog() {
//        if (enableLocationServiceDialog.isShowing) {
//            enableLocationServiceDialog.dismiss()
//        }
//    }
//
//    private fun showExitMapDialog() {
//        val binding: DialogExitMapBinding = DialogExitMapBinding.inflate(layoutInflater)
//        val dialogView = binding.root
//
//        binding.exitBtn.setOnClickListener {
//            intent = Intent(this@MapboxMapActivity, MapboxMapActivity::class.java)
//            startActivity(intent)
//            finish()
//
//            closeExitMapDialog()
//        }
//        binding.cancelBtn.setOnClickListener {
//            closeExitMapDialog()
//        }
//
//    }
    private fun showRegisterSellerStoreBottomSheet(location: Point) {
        val registerSellerStoreBottomSheetFragment =
            RegisterSellerStoreBottomSheetFragment.newInstance(
                location.longitude(),
                location.latitude()
            )
        registerSellerStoreBottomSheetFragment.show(
            supportFragmentManager,
            RegisterSellerStoreBottomSheetFragment.TAG
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(this@MapboxMapActivity, message, Toast.LENGTH_SHORT).show()
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
        if (granted) checkLocationService()
    }
}