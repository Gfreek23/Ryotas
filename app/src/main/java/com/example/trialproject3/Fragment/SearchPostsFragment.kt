package com.example.trialproject3.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.trialproject3.Activity.MainActivity
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Models.PostsModel
import com.example.trialproject3.R
import com.example.trialproject3.Utility.LoadingSpinnerOverlay
import com.example.trialproject3.databinding.FragmentSearchPostsBinding
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SearchPostsFragment : Fragment(), MainActivity.OnBackPressedListener {
    private val TAG = "SearchPostsFragment"
    private lateinit var binding: FragmentSearchPostsBinding
    private lateinit var loadingSpinnerOverlay: LoadingSpinnerOverlay
    private lateinit var context: Context
    private var selectedCategory: String? = null

    private var param1: String? = null
    private var param2: String? = null

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchPostsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchPostsBinding.inflate(inflater, container, false)

        context = requireContext()
        loadingSpinnerOverlay = LoadingSpinnerOverlay(context)

        binding.searchBar.requestFocus()

        binding.backArrow.setOnClickListener { backToPostsFragment() }

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
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedCategory = parent.getItemAtPosition(position).toString()
                if (position != 0)
                    binding.searchBar.setText("Category: $selectedCategory")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optionally do something when nothing is selected
            }
        }

        binding.searchBar.setOnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                if (binding.searchBar.text.toString().isNotEmpty())
                    if (binding.searchBar.text.toString().startsWith("Category: "))
                        searchPostsByCategory(selectedCategory!!)
                    else searchPosts(binding.searchBar.text.toString())
                true
            } else
                false

        }

        binding.searchBtn.setOnClickListener {
            if (binding.searchBar.text.toString().isNotEmpty())
                if (binding.searchBar.text.toString().startsWith("Category: "))
                    searchPostsByCategory(selectedCategory!!)
                else searchPosts(binding.searchBar.text.toString())
        }

        return binding.root
    }


    override fun onBackPressed(): Boolean {
        backToPostsFragment()
        return true
    }

    private fun backToPostsFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, PostsFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun searchPosts(query: String) {
        val postsReference = FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_POSTS)
            .whereEqualTo("productNameLowercase", query.toLowerCase(Locale.getDefault()))

        postsReference.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val postsList = mutableListOf<PostsModel>()
                    for (document in task.result) {
                        val posts = document.toObject(PostsModel::class.java)
                        postsList.add(posts)
                        Log.d(TAG, "searchProducts: $postsList")
                    }

                    val bundle = Bundle()
                    bundle.putSerializable("postsList", ArrayList(postsList))
                    val postsFragment = PostsFragment()
                    postsFragment.arguments = bundle

                    val fragmentManager = requireActivity().supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.fragmentContainer, postsFragment)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                } else
                    Log.e(TAG, "Error getting posts: ", task.exception)

            }
    }

    private fun searchPostsByCategory(query: String) {
        val postsReference = FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_POSTS)
            .whereEqualTo("postCategory", query)

        postsReference.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val postsList = mutableListOf<PostsModel>()
                    for (document in task.result) {
                        val posts = document.toObject(PostsModel::class.java)
                        postsList.add(posts)
                        Log.d(TAG, "searchProducts: $postsList")
                    }

                    val bundle = Bundle()
                    bundle.putSerializable("postsList", ArrayList(postsList))
                    val postsFragment = PostsFragment()
                    postsFragment.arguments = bundle

                    val fragmentManager = requireActivity().supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.fragmentContainer, postsFragment)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                } else
                    Log.e(TAG, "Error getting posts: ", task.exception)

            }
    }
}