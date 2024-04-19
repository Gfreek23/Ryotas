package com.example.trialproject3.BottomSheetModal

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.databinding.FragmentShowStoreInfoBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ShowStoreInfoBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentShowStoreInfoBottomSheetBinding
    private lateinit var onNavigateListener: OnNavigateListener

    interface OnNavigateListener {
        fun onNavigateClick(longitude: Double, latitude: Double)
    }

    companion object {
        const val TAG = "ShowStoreInfoBottomSheetFragment"
        private const val ARG_STORE_ID = "storeID"
        private const val ARG_LONGITUDE = "longitude"
        private const val ARG_LATITUDE = "latitude"
        fun newInstance(storeID: String, longitude: Double, latitude: Double) =
            ShowStoreInfoBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_STORE_ID, storeID)
                    putDouble(ARG_LONGITUDE, longitude)
                    putDouble(ARG_LATITUDE, latitude)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShowStoreInfoBottomSheetBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val storeID = arguments?.getString(ARG_STORE_ID)
        val longitude = arguments?.getDouble(ARG_LONGITUDE)
        val latitude = arguments?.getDouble(ARG_LATITUDE)
        if (isAdded && !storeID.isNullOrEmpty() && longitude != null && latitude != null) {
            loadStoreDetails(storeID)
            binding.navigateBtn.setOnClickListener {
                onNavigateListener.onNavigateClick(longitude, latitude)
                dismiss()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnNavigateListener) onNavigateListener = context
    }

    private fun loadStoreDetails(storeID: String) {
        val storeReference = FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_STORES)
            .document(storeID)

        storeReference.get()
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE

                if (task.isSuccessful) {
                    val storeSnapshot = task.result

                    val getStoreName = storeSnapshot.get("storeName").toString()
                    val getStoreLocation = storeSnapshot.get("storeLocation").toString()
//                    val getStoreName = storeSnapshot.get("storeName")

                    binding.storeNameTextView.text = getStoreName
                    binding.storeLocationTextView.text = getStoreLocation
                } else {

                }
            }
    }
}