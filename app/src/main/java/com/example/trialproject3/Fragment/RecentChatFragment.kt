package com.example.trialproject3.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trialproject3.Activity.MessageActivity
import com.example.trialproject3.Adapter.RecentChatUserAdapter
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Models.RecentChatUserModel
import com.example.trialproject3.Utility.LoadingSpinnerOverlay
import com.example.trialproject3.databinding.FragmentRecentChatBinding

class RecentChatFragment : Fragment(),
    RecentChatUserAdapter.OnRecentChatUserClickListener {
    private val TAG = "RecentChatFragment"
    private lateinit var binding: FragmentRecentChatBinding
    private lateinit var context: Context
    private lateinit var loadingSpinnerOverlay: LoadingSpinnerOverlay

    companion object {

        @JvmStatic
        fun newInstance() =
            RecentChatFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (loadingSpinnerOverlay.isShowing) {
            loadingSpinnerOverlay.dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecentChatBinding.inflate(inflater, container, false)

        binding.noChatTextView.visibility = View.GONE
        context = requireContext()
        loadingSpinnerOverlay = LoadingSpinnerOverlay(context)

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadRecentChatUsers()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) loadRecentChatUsers()
    }

    override fun onRecentChatUserClick(recentChatUserModel: RecentChatUserModel) {
        val intent = Intent(context, MessageActivity::class.java)
        intent.putExtra("chatUserData", recentChatUserModel)
        startActivity(intent)
    }

    private fun loadRecentChatUsers() {
        loadingSpinnerOverlay.showLoading()
        FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_USERS)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val recentChatUserModelList = ArrayList<RecentChatUserModel>()
                    for (document in it.result) {
                        val recentChatUserModel = RecentChatUserModel(
                            chatUserID = document.id,
                            chatUserName = document.getString("Fname").toString(),
                            chatUserProfilePicture = document.getString("ProfilePicture")
                                .toString(),
                            chatUserType = document.getString("UserType").toString(),
                        )
                        if (recentChatUserModel.chatUserType == "Seller" &&
                            recentChatUserModel.chatUserID != FirebaseHelper.currentUserID())
                            recentChatUserModelList.add(recentChatUserModel)
                    }
                    if (recentChatUserModelList.isNotEmpty()) {
                        binding.noChatTextView.visibility = View.GONE
                        binding.recentChatUsersRecyclerView.layoutManager =
                            LinearLayoutManager(context)
                        binding.recentChatUsersRecyclerView.adapter = RecentChatUserAdapter(
                            context,
                            recentChatUserModelList,
                            this
                        )
                    } else binding.noChatTextView.visibility = View.VISIBLE
                    loadingSpinnerOverlay.hideLoading()
                } else loadingSpinnerOverlay.hideLoading()
            }
    }
}