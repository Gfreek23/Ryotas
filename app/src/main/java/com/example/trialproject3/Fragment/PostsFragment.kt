package com.example.trialproject3.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trialproject3.Adapter.PostsAdapter
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Models.PostsModel
import com.example.trialproject3.R
import com.example.trialproject3.Utility.ToastHelper
import com.example.trialproject3.databinding.FragmentPostsBinding
import com.google.api.Distribution.BucketOptions.Linear
import java.util.EventListener


class PostsFragment : Fragment(), PostsAdapter.OnPostItemClickListener {
    private val TAG: String = "PostsFragment"
    private lateinit var binding: FragmentPostsBinding
    private lateinit var context: Context
    private lateinit var toastHelper: ToastHelper

    companion object {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsBinding.inflate(inflater, container, false)

        context = requireContext()
        toastHelper = ToastHelper(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isAdded) {
            loadPosts()
        }
    }

    private fun loadPosts() {
        val postsReference = FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_POSTS)
        postsReference.addSnapshotListener { value, error ->
            if (error != null) {
                Log.e(TAG, "loadPosts: $error")
                return@addSnapshotListener
            }

            if (value != null && !value.isEmpty) {
                val postsList = mutableListOf<PostsModel>()
                for (posts in value.documents) {
                    val post = posts.toObject(PostsModel::class.java)
                    if (post != null) {

                        postsList.add(post)
                    }
                }
                val postsAdapter = PostsAdapter(context, postsList, this@PostsFragment)
                binding.postsRecyclerView.layoutManager = LinearLayoutManager(context)
                binding.postsRecyclerView.adapter = postsAdapter
            }
        }
    }

    override fun onPostItemClick(postsModel: PostsModel) {
        goToFragment(postsModel)
    }

    private fun goToFragment(postsModel: PostsModel) {
        val fragment = PostUserProfileFragment()
        val bundle = Bundle()
        bundle.putString(
            PostUserProfileFragment.POST_USER_ID_PARAMS,
            postsModel.userID
        ) // Replace "your string value" with the actual string you want to pass
        fragment.arguments = bundle

        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}