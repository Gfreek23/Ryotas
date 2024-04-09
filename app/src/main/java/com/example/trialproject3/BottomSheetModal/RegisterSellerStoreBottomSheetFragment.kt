package com.example.trialproject3.BottomSheetModal

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Map.MapboxMapActivity
import com.example.trialproject3.R
import com.example.trialproject3.Utility.ToastHelper
import com.example.trialproject3.databinding.FragmentRegisterSellerStoreBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mapbox.api.geocoding.v5.GeocodingCriteria
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Point
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class RegisterSellerStoreBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentRegisterSellerStoreBottomSheetBinding
    private lateinit var context: Context
    private lateinit var storeLocation: String
    private lateinit var toastHelper: ToastHelper

    companion object {
        const val TAG = "RegisterSellerStoreBottomSheetFragment"
        private const val LOCATION_LONGITUDE = "locationLongitude"
        private const val LOCATION_LATITUDE = "locationLatitude"

        @JvmStatic
        fun newInstance(locationLongitude: Double, locationLatitude: Double) =
            RegisterSellerStoreBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putDouble(LOCATION_LONGITUDE, locationLongitude)
                    putDouble(LOCATION_LATITUDE, locationLatitude)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterSellerStoreBottomSheetBinding.inflate(inflater, container, false)

        context = requireContext()
        toastHelper = ToastHelper(context)
        binding.progressBar.visibility = View.GONE

        binding.registerBtn.setOnClickListener {
            val storeName = binding.storeNameEditText.text.toString()
            val storeDescription = binding.descriptionEditText.text.toString()
            if (storeName.isEmpty()) {
                binding.storeNameEditText.error = "Enter Store name"
                binding.storeNameEditText.requestFocus()
            } else if (storeDescription.isEmpty()) {
                binding.descriptionEditText.error = "Enter Description"
                binding.descriptionEditText.requestFocus()
            } else {
                binding.registerBtn.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                getUserDetails(storeName, storeDescription, storeLocation)


            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isAdded) {
            val locationLongitude = arguments?.getDouble(LOCATION_LONGITUDE)
            val locationLatitude = arguments?.getDouble(LOCATION_LATITUDE)

            geocodeStoreLocation()
        }
    }

    private fun getUserDetails(storeName: String, storeDescription: String, storeLocation: String) {
        FirebaseHelper.currentUserDetails().get()
            .addOnCompleteListener { task ->
                binding.registerBtn.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    val documentSnapshot = task.result
                    val getFullName = documentSnapshot.getString("Fname")!!
                    registerStore(
                        storeName,
                        storeDescription,
                        storeLocation,
                        getFullName,
                        generateRandomStoreID()
                    )
                } else {
                    Log.e(TAG, "getUserDetails: " + task.exception)
                }
            }
    }

    private fun registerStore(
        storeName: String,
        storeDescription: String,
        storeLocation: String,
        ownerName: String,
        storeID: String
    ) {
        val store: MutableMap<String, Any> = HashMap()
        store["storeID"] = storeID
        store["storeName"] = storeName
        store["storeOwnerID"] = FirebaseHelper.currentUserID()
        store["storeOwner"] = ownerName
        store["storeDescription"] = storeDescription
        store["storeLocation"] = storeLocation
        store["storeLongitude"] = MapboxMapActivity.currentLongitude
        store["storeLatitude"] = MapboxMapActivity.currentLatitude

        FirebaseHelper.getFireStoreInstance().collection(FirebaseHelper.KEY_COLLECTION_STORES)
            .document(storeID)
            .set(store)
            .addOnCompleteListener { task ->
                binding.registerBtn.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    toastHelper.showToast("Store Registered", 0)
                    dismiss()
                } else {
                    Log.e(TAG, "registerStore: " + task.exception)
                }
            }
    }

    private fun generateRandomStoreID(): String {
        val uuid = UUID.randomUUID()
        return uuid.toString()
    }

    private fun geocodeStoreLocation() {
        //pickup location geocode
        val pickupLocationGeocode = MapboxGeocoding.builder()
            .accessToken(context.getString(R.string.mapbox_access_token))
            .query(
                Point.fromLngLat(
                    MapboxMapActivity.currentLongitude,
                    MapboxMapActivity.currentLatitude
                )
            )
            .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
            .build()

        pickupLocationGeocode.enqueueCall(object : Callback<GeocodingResponse?> {
            @SuppressLint("SetTextI18n", "LongLogTag")
            override fun onResponse(
                call: Call<GeocodingResponse?>,
                response: Response<GeocodingResponse?>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null && response.body()!!.features().isNotEmpty()) {
                        val feature = response.body()!!.features()[0]
                        val locationName = feature.placeName()
                        storeLocation = locationName!!
                        binding.storeLocationEditText.text = locationName
//                        pickupLocationGeocodeResult = 1
//                        isLoading = false

                    } else {
//                        isLoading = false
                        binding.storeLocationEditText.text = "Location not found"
                    }
                } else {
//                    isLoading = false

                    Log.e(
                        TAG,
                        "geocodeDestination: onResponse - Geocode error ${response.message()}"
                    )
                    binding.storeLocationEditText.text = "Location not found"
                }

//                if (!isLoading) {
//                    binding.pickupLocationTextView.visibility = View.VISIBLE
//                    binding.pickupLocationLoading.visibility = View.GONE
//                    binding.destinationLoading.visibility = View.GONE
//                }
            }

            @SuppressLint("SetTextI18n", "LongLogTag")
            override fun onFailure(call: Call<GeocodingResponse?>, t: Throwable) {
                Log.e(TAG, t.message.toString())
                binding.storeLocationEditText.text = "Location not found"
            }
        })
    }

}