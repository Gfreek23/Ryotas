package com.example.trialproject3.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.example.trialproject3.Activity.MainActivity
import com.example.trialproject3.Activity.MessageActivity
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Models.RecentChatUserModel
import com.example.trialproject3.Utility.FragmentManagerHelper
import com.example.trialproject3.Utility.LoadingSpinnerOverlay
import com.example.trialproject3.databinding.FragmentPostUserProfileBinding


class PostUserProfileFragment : Fragment(),
    MainActivity.OnBackPressedListener {
    private lateinit var binding: FragmentPostUserProfileBinding
    private lateinit var context: Context
    private lateinit var loadingSpinnerOverlay: LoadingSpinnerOverlay
    private lateinit var fragmentManagerHelper: FragmentManagerHelper

    companion object {
        const val TAG: String = "PostUserProfileFragment"
        const val POST_USER_ID_PARAMS = "postID"

        @JvmStatic
        fun newInstance(postID: String) =
            PostUserProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(POST_USER_ID_PARAMS, postID)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostUserProfileBinding.inflate(inflater, container, false)

        context = requireContext()
        loadingSpinnerOverlay = LoadingSpinnerOverlay(context)
        fragmentManagerHelper = FragmentManagerHelper(activity as FragmentActivity)

        binding.backBtn.setOnClickListener { fragmentManagerHelper.showFragment(HomeFragment()) }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postID = arguments?.getString(POST_USER_ID_PARAMS)
        if (isAdded && !postID.isNullOrEmpty()) {
            loadPostUserDetails(postID)
        }
    }

    override fun onBackPressed(): Boolean {
        fragmentManagerHelper.showFragment(HomeFragment())
        return true
    }


    private fun loadPostUserDetails(postID: String) {
        loadingSpinnerOverlay.showLoading()
        FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_POSTS)
            .document(postID)
            .get()
            .addOnCompleteListener { task ->
                loadingSpinnerOverlay.hideLoading()
                binding.profilePictureProgressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    val userSnapshot = task.result

                    val getFullName = userSnapshot.getString("fullName")
                    val getEmail = userSnapshot.getString("email")
                    val getPhone = userSnapshot.getString("phoneNumber")
                    val getProfilePicture = userSnapshot.getString("userPostImage")
                    val getStoreName = userSnapshot.getString("storeName")
                    val getStoreLocation = userSnapshot.getString("storeLocation")

                    binding.fullNameTextView.text = getFullName
                    binding.emailTextView.text = getEmail
                    binding.phoneTextView.text = getPhone
                    binding.storeNameTextView.text = getStoreName
                    binding.storeLocationTextView.text = getStoreLocation

                    if (getProfilePicture != "none") {
                        Glide.with(requireContext())
                            .load(getProfilePicture)
                            .into(binding.profilePicImageView)
                    }

                    val recentChatUserModel = RecentChatUserModel(
                        chatUserID = userSnapshot.getString("userID")!!,
                        chatUserName = getFullName!!,
                        chatUserProfilePicture = getProfilePicture!!
                    )

                    binding.chatBtn.setOnClickListener {
                        val intent = Intent(context, MessageActivity::class.java)
                        intent.putExtra("chatUserData", recentChatUserModel)
                        startActivity(intent)
                    }
                } else {
                    Log.e(TAG, "loadPostUserDetails: " + task.exception)
                }
            }
    }
}