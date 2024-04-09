package com.example.trialproject3.Fragment

import android.content.Context
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
import com.example.trialproject3.databinding.FragmentPostsBinding

class PostsFragment : Fragment(), MainActivity.OnBackPressedListener {
    private val TAG = "PostsFragment"
    private lateinit var binding: FragmentPostsBinding
    private lateinit var toastHelper: ToastHelper
    private lateinit var context: Context
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsBinding.inflate(inflater, container, false)

        context = requireContext()
        toastHelper = ToastHelper(context)
        binding.progressBar.visibility = View.GONE

        binding.postBtn.setOnClickListener { postContent() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) {

        }
    }

    override fun onBackPressed(): Boolean {
        backToHomeFragment()
        return true
    }

    private fun backToHomeFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, HomeFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun postContent() {
        val title = binding.titleEditText.text.toString()
        val content = binding.contentEditText.text.toString()

        if (title.isEmpty()) {
            binding.titleEditText.requestFocus()
            binding.titleEditText.error = "Enter title"
        } else if (content.isEmpty()) {
            binding.contentEditText.requestFocus()
            binding.contentEditText.error = "Enter content"
        } else {
            binding.progressBar.visibility = View.VISIBLE
            binding.postBtn.visibility = View.GONE

            val post = HashMap<Any, String>()
            post["userID"] = FirebaseHelper.currentUserID()
            post["Fname"] = MainActivity.fullName
            post["title"] = title
            post["content"] = content

            FirebaseHelper.getFireStoreInstance().collection(FirebaseHelper.KEY_COLLECTION_POSTS)
                .document().set(post)
                .addOnCompleteListener { task ->
                    binding.progressBar.visibility = View.GONE
                    binding.postBtn.visibility = View.VISIBLE

                    if (task.isSuccessful) {
                        binding.titleEditText.setText("")
                        binding.contentEditText.setText("")
                        toastHelper.showToast("Content posted", 0)
                    } else {
                        toastHelper.showToast("Content failed to post", 1)
                        Log.e(TAG, "postContent: ${task.result}")
                    }
                }
        }
    }


}