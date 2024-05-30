package com.example.trialproject3.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trialproject3.Adapter.PostsAdapter
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Models.PostsModel
import com.example.trialproject3.Utility.FragmentManagerHelper
import com.example.trialproject3.Utility.LoadingSpinnerOverlay
import com.example.trialproject3.Utility.ToastHelper
import com.example.trialproject3.databinding.FragmentPostsBinding


class PostsFragment : Fragment(),
    PostsAdapter.OnPostItemClickListener,
    PostsAdapter.OnPostRateClickListener {
    private val TAG: String = "PostsFragment"
    private lateinit var binding: FragmentPostsBinding
    private lateinit var context: Context
    private lateinit var toastHelper: ToastHelper
    private lateinit var loadingSpinnerOverlay: LoadingSpinnerOverlay
    private lateinit var fragmentManagerHelper: FragmentManagerHelper

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsBinding.inflate(inflater, container, false)

        context = requireContext()
        toastHelper = ToastHelper(context)
        loadingSpinnerOverlay = LoadingSpinnerOverlay(context)
        fragmentManagerHelper = FragmentManagerHelper(activity as FragmentActivity)

        binding.noPostsTextView.visibility = View.GONE

        binding.searchBar.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                fragmentManagerHelper.showFragment(SearchPostsFragment())
            }
            false
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadPosts()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) {
            val postsList = arguments?.getSerializable("postsList") as? ArrayList<PostsModel>
            if (postsList != null) loadSearchedPosts(postsList)
            else loadPosts()

        }
    }

    private fun loadSearchedPosts(postsLists: ArrayList<PostsModel>) {
        loadingSpinnerOverlay.showLoading()
        val postsAdapter = PostsAdapter(context, postsLists, this, this)
        binding.postsRecyclerView.adapter = postsAdapter
        binding.postsRecyclerView.layoutManager = LinearLayoutManager(context).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        if (postsLists.isEmpty()) binding.noPostsTextView.visibility = View.VISIBLE
        else binding.noPostsTextView.visibility = View.GONE

        loadingSpinnerOverlay.hideLoading()
    }

    private fun loadPosts() {
        binding.noPostsTextView.visibility = View.GONE
        loadingSpinnerOverlay.showLoading()
        val postsReference = FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_POSTS)
        postsReference.addSnapshotListener { value, error ->
            if (error != null) {
                Log.e(TAG, "loadPosts: $error")
                loadingSpinnerOverlay.hideLoading()
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
                val postsAdapter =
                    PostsAdapter(context, postsList, this@PostsFragment, this@PostsFragment)
                binding.postsRecyclerView.layoutManager = LinearLayoutManager(context).apply {
                    reverseLayout = true
                    stackFromEnd = true
                }
                binding.postsRecyclerView.adapter = postsAdapter
            }
            loadingSpinnerOverlay.hideLoading()
        }
    }

    override fun onPostItemClick(postsModel: PostsModel) {
        fragmentManagerHelper.showFragment(PostUserProfileFragment.newInstance(postsModel.postID))
    }

    override fun onPostRateClick(postsModel: PostsModel, rating: Float) {
        rateUser(postsModel, rating)
    }

    private fun rateUser(postsModel: PostsModel, rating: Float) {
        loadingSpinnerOverlay.showLoading()
        val userReference = FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_USERS)
            .document(postsModel.userID)

        userReference.update("sellerRatings", rating)
            .addOnCompleteListener { task ->
                loadingSpinnerOverlay.hideLoading()
                if (task.isSuccessful) {
                    toastHelper.showToast("User Rated", 0)
                } else {
                    toastHelper.showToast("User failed to rated", 1)
                    Log.e(TAG, "rateUser: " + task.exception)
                }
            }

    }
}