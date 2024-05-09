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
import androidx.fragment.app.Fragment
import com.example.trialproject3.Activity.MainActivity
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.R
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
    private lateinit var context: Context
    private var postImageUri: Uri? = null
    private var postImage: String = "none"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostContentBinding.inflate(inflater, container, false)

        context = requireContext()
        toastHelper = ToastHelper(context)
        binding.progressBar.visibility = View.GONE

        binding.addImageView.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
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
        } else {
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

            if (postImageUri != null) {
                val storageRef = FirebaseStorage.getInstance().reference
                val postImageRef = storageRef.child("/postsImages/${UUID.randomUUID()}")

                postImageRef.putFile(postImageUri!!)
                    .addOnCompleteListener { uploadTask ->
                        if (uploadTask.isSuccessful) {
                            postImageRef.downloadUrl
                                .addOnSuccessListener {
                                    postImage = it.toString()



                                    val post = HashMap<Any, String>()
                                    post["userID"] = FirebaseHelper.currentUserID()
                                    post["fullName"] = fullName!!
                                    post["userPostImage"] = profilePicture!!
                                    post["userType"] = userType!!
                                    post["email"] = FirebaseHelper.currentUser().email.toString()
                                    post["phoneNumber"] = phoneNumber!!
                                    post["title"] = title
                                    post["description"] = description
                                    post["postImage"] = postImage
                                    post["storeName"] = MainActivity.storeName!!
                                    post["storeLocation"] = MainActivity.storeLocation!!
                                    post["timePosted"] = formattedDateTime

                                    FirebaseHelper.getFireStoreInstance()
                                        .collection(FirebaseHelper.KEY_COLLECTION_POSTS)
                                        .document().set(post)
                                        .addOnCompleteListener { task ->
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
                val post = HashMap<Any, String>()
                post["userID"] = FirebaseHelper.currentUserID()
                post["fullName"] = fullName!!
                post["userPostImage"] = profilePicture!!
                post["userType"] = userType!!
                post["email"] = FirebaseHelper.currentUser().email.toString()
                post["phoneNumber"] = phoneNumber!!
                post["title"] = title
                post["description"] = description
                post["postImage"] = postImage
                post["storeName"] = MainActivity.storeName!!
                post["storeLocation"] = MainActivity.storeLocation!!
                post["timePosted"] = formattedDateTime

                FirebaseHelper.getFireStoreInstance()
                    .collection(FirebaseHelper.KEY_COLLECTION_POSTS)
                    .document().set(post)
                    .addOnCompleteListener { task ->
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