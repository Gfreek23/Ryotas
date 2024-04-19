package com.example.trialproject3.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.example.trialproject3.Activity.MainActivity
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.R
import com.example.trialproject3.databinding.FragmentPostUserProfileBinding


class PostUserProfileFragment : Fragment(), MainActivity.OnBackPressedListener {
    private lateinit var binding: FragmentPostUserProfileBinding

    companion object {
        const val TAG: String = "PostUserProfileFragment"
        const val POST_USER_ID_PARAMS = "postUserID"

        @JvmStatic
        fun newInstance(postUserID: String) =
            PostUserProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(POST_USER_ID_PARAMS, postUserID)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostUserProfileBinding.inflate(inflater, container, false)

        binding.backBtn.setOnClickListener { backToFragment() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postUserID = arguments?.getString(POST_USER_ID_PARAMS)
        if (isAdded && !postUserID.isNullOrEmpty()) {
            loadPostUserDetails(postUserID)
        }
    }

    override fun onBackPressed(): Boolean {
        backToFragment()

        return true
    }

    private fun backToFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, PostsFragment())
        fragmentTransaction.commit()
    }

    private fun loadPostUserDetails(postUserID: String) {
        FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_USERS)
            .document(postUserID)
            .get()
            .addOnCompleteListener { task ->
                binding.profilePictureProgressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    val userSnapshot = task.result

                    val getFullName = userSnapshot.getString("Fname")
                    val getEmail = userSnapshot.getString("email")
                    val getPhone = userSnapshot.getString("phoneNumber")
                    val getProfilePicture = userSnapshot.getString("ProfilePicture")
                    val getUserType = userSnapshot.getString("UserType")
                    val getStoreName = userSnapshot.getString("storeName")
                    val getStoreLocation = userSnapshot.getString("storeLocation")

                    binding.fullNameTextView.text = getFullName
                    binding.emailTextView.text = getEmail
                    binding.phoneTextView.text = getPhone
                    binding.userTypeTextView.text = getUserType
                    binding.storeNameTextView.text = getStoreName
                    binding.storeLocationTextView.text = getStoreLocation

                    if (getProfilePicture != "none") {
                        Glide.with(requireContext())
                            .load(getProfilePicture)
                            .into(binding.profilePicImageView)
                    }
                } else {
                    Log.e(TAG, "loadPostUserDetails: " + task.exception)
                }
            }
    }
}