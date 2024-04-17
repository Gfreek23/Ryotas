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
import com.example.trialproject3.databinding.FragmentPostsBinding
import com.google.api.Distribution.BucketOptions.Linear
import java.util.EventListener


class PostsFragment : Fragment() {
    private val TAG : String = "PostsFragment"
    private lateinit var binding : FragmentPostsBinding
    private lateinit var context : Context
    companion object {

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsBinding.inflate(inflater, container, false)

        context = requireContext()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(isAdded) {
            loadPosts()
        }
    }

    private fun loadPosts(){
        val postsReference = FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_POSTS)
        postsReference.addSnapshotListener{ value, error ->
            if (error != null){
                Log.e(TAG, "loadPosts: $error")
                return@addSnapshotListener
            }

            if (value != null && !value.isEmpty){
                val postsList = mutableListOf<PostsModel>()
                for (posts in value.documents){
                    val post = posts.toObject(PostsModel::class.java)
                    if (post !=  null){

                      postsList.add(post)
                    }
                }
                val postsAdapter = PostsAdapter(context, postsList)
                binding.postsRecyclerView.layoutManager = LinearLayoutManager(context)
                binding.postsRecyclerView.adapter = postsAdapter
            }
        }
    }
}