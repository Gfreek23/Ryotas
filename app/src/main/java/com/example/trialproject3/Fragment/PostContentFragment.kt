package com.example.trialproject3.Fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.trialproject3.Activity.MainActivity
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Models.PostsModel
import com.example.trialproject3.R
import com.example.trialproject3.Utility.LoadingSpinnerOverlay
import com.example.trialproject3.Utility.ToastHelper
import com.example.trialproject3.databinding.FragmentPostContentBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PostContentFragment : Fragment(), MainActivity.OnBackPressedListener {
    private val TAG = "PostsFragment"
    private lateinit var binding: FragmentPostContentBinding
    private lateinit var toastHelper: ToastHelper
    private lateinit var loadingSpinnerOverlay: LoadingSpinnerOverlay
    private lateinit var context: Context
    private var postImageUri: Uri? = null
    private var postImage: String = "none"
    private var selectedCategory: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostContentBinding.inflate(inflater, container, false)

        context = requireContext()
        toastHelper = ToastHelper(context)
        loadingSpinnerOverlay = LoadingSpinnerOverlay(context)
        binding.progressBar.visibility = View.GONE

        binding.postImageView.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        val postCategorySpinner = binding.postCategorySpinner
        val postCategories = resources.getStringArray(R.array.post_categories)
        val adapter = object : ArrayAdapter<String>(
            context,
            R.layout.spinner_item,
            postCategories
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }
        }
        adapter.setDropDownViewResource(R.layout.spinner_item)
        postCategorySpinner.adapter = adapter

        postCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedCategory = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optionally do something when nothing is selected
            }
        }

        binding.postBtn.setOnClickListener { postContent() }

        return binding.root
    }

    override fun onBackPressed(): Boolean {
        goToFragment(HomeFragment())
        return true
    }

    private fun goToFragment(fragment: Fragment) {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun postContent() {
        val title = binding.titleEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()

        if (title.isEmpty()) {
            binding.titleEditText.requestFocus()
            binding.titleEditText.error = "Enter title"
        } else if (description.isEmpty()) {
            binding.descriptionEditText.requestFocus()
            binding.descriptionEditText.error = "Enter content"

        } else if (selectedCategory != null) {
            binding.postCategorySpinner.requestFocus()
            toastHelper.showToast("Select a category", 1)
        } else {
            loadingSpinnerOverlay.showLoading()
            binding.progressBar.visibility = View.VISIBLE
            binding.postBtn.visibility = View.GONE

            val sharedPreferences = context.getSharedPreferences(
                "currentUserPrefs",
                Context.MODE_PRIVATE
            )

            val fullName = sharedPreferences.getString("fullName", null)
            val userType = sharedPreferences.getString("userType", null)
            val profilePicture =
                sharedPreferences.getString("profilePicture", null)
            val phoneNumber =
                sharedPreferences.getString("phoneNumber", null)

            val currentTimeMillis = System.currentTimeMillis()
            val sdf = SimpleDateFormat("MMMM-yyyy-dd hh:mm:ss a", Locale.getDefault())
            val currentDate = Date(currentTimeMillis)
            val formattedDateTime = sdf.format(currentDate)

            //post content with image
            if (postImageUri != null) {
                val storageRef = FirebaseStorage.getInstance().reference
                val postImageRef = storageRef.child("/postsImages/${UUID.randomUUID()}")

                postImageRef.putFile(postImageUri!!)
                    .addOnCompleteListener { uploadTask ->
                        if (uploadTask.isSuccessful) {
                            postImageRef.downloadUrl
                                .addOnSuccessListener {
                                    postImage = it.toString()

                                    val postsModel = PostsModel(
                                        userID = FirebaseHelper.currentUserID(),
                                        fullName = fullName!!,
                                        userType = userType!!,
                                        userPostImage = profilePicture!!,
                                        email = FirebaseHelper.currentUser().email.toString(),
                                        phoneNumber = phoneNumber!!,
                                        title = title,
                                        titleLowerCase = title.toLowerCase(Locale.getDefault()),
                                        category = selectedCategory!!,
                                        description = description,
                                        postImage = postImage,
                                        storeLocation = MainActivity.storeLocation!!,
                                        storeName = MainActivity.storeName!!,
                                        timePosted = formattedDateTime
                                    )

                                    FirebaseHelper.getFireStoreInstance()
                                        .collection(FirebaseHelper.KEY_COLLECTION_POSTS)
                                        .document().set(postsModel)
                                        .addOnCompleteListener { task ->
                                            loadingSpinnerOverlay.hideLoading()
                                            binding.progressBar.visibility = View.GONE
                                            binding.postBtn.visibility = View.VISIBLE

                                            if (task.isSuccessful) {
                                                binding.titleEditText.setText("")
                                                binding.descriptionEditText.setText("")
                                                goToFragment(PostsFragment())
                                                toastHelper.showToast("Content posted", 0)
                                            } else {
                                                toastHelper.showToast("Content failed to post", 1)
                                                Log.e(TAG, "postContent: ${task.result}")
                                            }
                                        }
                                }
                                .addOnFailureListener {
                                    Log.e(TAG, "postContent: " + it.message)
                                }
                        } else {
                            Log.e(TAG, "postContent: " + uploadTask.exception)
                        }
                    }
            } else {
                //post content without image
                val postsModel = PostsModel(
                    userID = FirebaseHelper.currentUserID(),
                    fullName = fullName!!,
                    userType = userType!!,
                    userPostImage = profilePicture!!,
                    email = FirebaseHelper.currentUser().email.toString(),
                    phoneNumber = phoneNumber!!,
                    title = title,
                    titleLowerCase = title.toLowerCase(Locale.getDefault()),
                    category = selectedCategory!!,
                    description = description,
                    storeLocation = MainActivity.storeLocation!!,
                    storeName = MainActivity.storeName!!,
                    timePosted = formattedDateTime
                )

                FirebaseHelper.getFireStoreInstance()
                    .collection(FirebaseHelper.KEY_COLLECTION_POSTS)
                    .document().set(postsModel)
                    .addOnCompleteListener { task ->
                        loadingSpinnerOverlay.hideLoading()
                        binding.progressBar.visibility = View.GONE
                        binding.postBtn.visibility = View.VISIBLE

                        if (task.isSuccessful) {
                            binding.titleEditText.setText("")
                            binding.descriptionEditText.setText("")
                            goToFragment(PostsFragment())
                            toastHelper.showToast("Content posted", 0)
                        } else {
                            toastHelper.showToast("Content failed to post", 1)
                            Log.e(TAG, "postContent: ${task.result}")
                        }
                    }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                postImageUri = data.data
                binding.postImageView.setImageURI(postImageUri)
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            toastHelper.showToast(ImagePicker.getError(data), 1)
        } else {
            toastHelper.showToast("Task Cancelled", 1)
        }
    }
}