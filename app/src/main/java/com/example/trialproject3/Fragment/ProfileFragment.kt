package com.example.trialproject3.Fragment

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.trialproject3.Activity.LoginActivity
import com.example.trialproject3.Activity.MainActivity.OnBackPressedListener
import com.example.trialproject3.Activity.ToReceiveActivity
import com.example.trialproject3.Activity.ToShipActivity
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Helper.AlertDialogHelper
import com.example.trialproject3.Map.MapboxMapActivity
import com.example.trialproject3.Models.StoreDetailsModel
import com.example.trialproject3.R
import com.example.trialproject3.Utility.LoadingSpinnerOverlay
import com.example.trialproject3.Utility.ToastHelper
import com.example.trialproject3.databinding.FragmentProfileBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment : Fragment(), OnBackPressedListener {
    private val TAG = "ProfileFragment"
    private lateinit var context: Context
    private lateinit var binding: FragmentProfileBinding
    private lateinit var intent: Intent
    private lateinit var alertDialogHelper: AlertDialogHelper
    private lateinit var toastHelper: ToastHelper
    private var profilePicture: String = "none"
    private lateinit var loadingSpinnerOverlay: LoadingSpinnerOverlay

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        context = requireContext()
        alertDialogHelper = AlertDialogHelper(context)
        toastHelper = ToastHelper(context)
        loadingSpinnerOverlay = LoadingSpinnerOverlay(context)

        binding.profilePictureProgressBar.visibility = View.GONE
        binding.storeInfoLayout.visibility = View.GONE
        binding.storeDisplayLayout.visibility = View.GONE
        binding.registerStoreLayout.visibility = View.GONE
        binding.toShipBtn.visibility = View.GONE
        binding.toPayBtn.visibility = View.GONE
        binding.toReceiveBtn.visibility = View.GONE

        binding.backBtn.setOnClickListener { backToHomeFragment() }

        binding.uploadImageView.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        binding.logoutBtn.setOnClickListener {
            alertDialogHelper.showAlertDialog(
                "Logout",
                "Are you sure you want to Logout?",
                "Cancel",
                { dialog: DialogInterface?, which: Int -> alertDialogHelper.dismissDialog() },
                "Logout"
            ) { dialog: DialogInterface?, which: Int -> logoutUser() }
        }
        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) {
            loadUserProfile()
        }
    }

    override fun onBackPressed(): Boolean {
        backToHomeFragment()
        return true
    }

    private fun logoutUser() {
        loadingSpinnerOverlay.showLoading()
        FirebaseHelper.signOutUser()
        intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
        loadingSpinnerOverlay.hideLoading()
    }

    private fun loadUserProfile() {
        loadingSpinnerOverlay.showLoading()
        if (FirebaseHelper.currentUser() != null) {
            FirebaseHelper.currentUserDetails().get()
                .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                    loadingSpinnerOverlay.hideLoading()
                    if (task.isSuccessful) {
                        val documentSnapshot = task.result
                        val getFirstName = documentSnapshot.getString("Fname")
                        val getUserType = documentSnapshot.getString("UserType")
                        val getProfilePicture = documentSnapshot.get("ProfilePicture")
                        val getPhone: String = documentSnapshot.get("Phone").toString()
                        binding.fullNameTextView.text = getFirstName
                        binding.userTypeTextView.text = getUserType
                        binding.emailTextView.text = FirebaseHelper.currentUser().email
                        binding.phoneTextView.text = getPhone

                        if (getProfilePicture != "none") {
                            Glide.with(context)
                                .load(getProfilePicture)
                                .placeholder(R.drawable.loading_gif)
                                .into(binding.profilePicImageView)
                        }

                        if (getUserType == "Seller") {
                            binding.storeInfoLayout.visibility = View.VISIBLE
                            val storeReference = FirebaseHelper.getFireStoreInstance()
                                .collection(FirebaseHelper.KEY_COLLECTION_STORES)

                            storeReference.addSnapshotListener { value, error ->
                                if (error != null) {
                                    Log.e(TAG, "loadUserProfile: " + error.message)
                                    return@addSnapshotListener
                                }

                                if (value != null && !value.isEmpty) {
                                    val storeDetailsModelList =
                                        mutableListOf<StoreDetailsModel>()
                                    for (stores in value.documents) {
                                        val storeDetailsModel =
                                            stores.toObject(StoreDetailsModel::class.java)
                                        if (storeDetailsModel != null) {
                                            if (storeDetailsModel.storeOwnerID == FirebaseHelper.currentUserID()) {
                                                storeDetailsModelList.add(storeDetailsModel)
                                                binding.storeDisplayLayout.visibility = View.VISIBLE

                                                binding.storeNameTextView.text =
                                                    storeDetailsModel.storeName
                                                binding.storeLocationTextView.text =
                                                    storeDetailsModel.storeLocation
                                                binding.addAnotherStoreBtn.setOnClickListener {
                                                    val intent =
                                                        Intent(
                                                            context,
                                                            MapboxMapActivity::class.java
                                                        )
                                                    startActivity(intent)
                                                    requireActivity().finish()
                                                }
                                            } else {
                                                binding.registerStoreLayout.visibility =
                                                    View.VISIBLE
                                                binding.registerStoreBtn.setOnClickListener {
                                                    val intent =
                                                        Intent(
                                                            context,
                                                            MapboxMapActivity::class.java
                                                        )
                                                    startActivity(intent)
                                                    requireActivity().finish()
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            binding.toShipBtn.visibility = View.VISIBLE
                            binding.toPayBtn.visibility = View.VISIBLE
                            binding.toReceiveBtn.visibility = View.VISIBLE

                            binding.toShipBtn.setOnClickListener {
                                intent = Intent(context, ToShipActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                            binding.toPayBtn.setOnClickListener {
                                intent = Intent(context, ToShipActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                            binding.toReceiveBtn.setOnClickListener {
                                intent = Intent(context, ToReceiveActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                        }
                    } else {
                        Log.e(TAG, "loadUserProfile: " + task.exception)
                    }
                }
        }
    }

    private fun updateProfilePicture(profilePictureUri: Uri) {
        loadingSpinnerOverlay.showLoading()
        binding.profilePictureProgressBar.visibility = View.VISIBLE
        binding.uploadImageView.visibility = View.GONE

        val storageRef = FirebaseStorage.getInstance().reference
        val profilePictureRef =
            storageRef.child("/profilePictures/${FirebaseHelper.currentUserID()}")

        profilePictureRef.putFile(profilePictureUri)
            .addOnCompleteListener { uploadTask ->
                if (uploadTask.isSuccessful) {
                    profilePictureRef.downloadUrl
                        .addOnSuccessListener {
                            profilePicture = it.toString()
                            FirebaseHelper.currentUserDetails()
                                .update("ProfilePicture", profilePicture)
                                .addOnCompleteListener { task ->
                                    loadingSpinnerOverlay.hideLoading()
                                    binding.profilePictureProgressBar.visibility = View.GONE
                                    binding.uploadImageView.visibility = View.VISIBLE

                                    if (task.isSuccessful) {
                                        binding.profilePicImageView.setImageURI(profilePictureUri)
                                        toastHelper.showToast("Profile picture updated", 0)
                                    } else {
                                        toastHelper.showToast(
                                            "Profile picture failed to update",
                                            1
                                        )
                                    }
                                }
                        }
                        .addOnFailureListener {
                            toastHelper.showToast(
                                "Profile picture failed to update",
                                1
                            )
                            Log.e(TAG, "updateProfilePicture: " + it.message)
                        }

                } else {
                    toastHelper.showToast(
                        "Profile picture failed to update",
                        1
                    )
                    Log.e(TAG, "updateProfilePicture: " + uploadTask.exception)
                }
            }
    }

    private fun backToHomeFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, HomeFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val uri = data.data!!
                updateProfilePicture(uri)
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            toastHelper.showToast(ImagePicker.getError(data), 1)
        } else {
            toastHelper.showToast("Task Cancelled", 1)
        }
    }
}