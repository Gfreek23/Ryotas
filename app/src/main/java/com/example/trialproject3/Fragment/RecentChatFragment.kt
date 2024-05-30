package com.example.trialproject3.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trialproject3.Activity.MessageActivity
import com.example.trialproject3.Adapter.RecentChatUserAdapter
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Models.MessageModel
import com.example.trialproject3.Models.RecentChatUserModel
import com.example.trialproject3.Utility.LoadingSpinnerOverlay
import com.example.trialproject3.databinding.FragmentRecentChatBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class RecentChatFragment : Fragment(), RecentChatUserAdapter.OnRecentChatUserClickListener {
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

    //TODO: Implement the logic to load recent chat users
    private fun loadRecentChatUsers() {
        loadingSpinnerOverlay.showLoading()
        val recentChatUsersList = ArrayList<RecentChatUserModel>()
        val addedUserIDs = HashSet<String>()
        val currentUserID = FirebaseHelper.currentUserID()

        val database = Firebase.database
        val messageReference = database.getReference(FirebaseHelper.KEY_COLLECTION_MESSAGES)

        messageReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                loadingSpinnerOverlay.hideLoading()
                if (snapshot.exists()) {
                    for (messageSnapshot in snapshot.children) {
                        val messageModel = messageSnapshot.getValue(MessageModel::class.java)
                        if (messageModel != null) {
                            if (messageModel.senderID == currentUserID || messageModel.receiverID == currentUserID) {
                                val chatUserID =
                                    if (messageModel.senderID == currentUserID) messageModel.receiverID
                                    else messageModel.senderID

                                if (chatUserID.isNotEmpty() && !addedUserIDs.contains(chatUserID)) {
                                    FirebaseHelper.getFireStoreInstance()
                                        .collection(FirebaseHelper.KEY_COLLECTION_USERS)
                                        .document(chatUserID)
                                        .get()
                                        .addOnSuccessListener { userDocument ->
                                            val recentChatUserModel = RecentChatUserModel(
                                                chatUserID = userDocument.id,
                                                chatUserName = userDocument.data?.get("Fname")
                                                    ?.toString() ?: "",
                                                chatUserProfilePicture = userDocument.data?.get("ProfilePicture")
                                                    ?.toString() ?: "",
                                            )
                                            recentChatUsersList.add(recentChatUserModel)
                                            addedUserIDs.add(chatUserID)
                                            binding.recentChatUsersRecyclerView.layoutManager =
                                                LinearLayoutManager(context)
                                            binding.recentChatUsersRecyclerView.adapter =
                                                RecentChatUserAdapter(
                                                    context,
                                                    recentChatUsersList,
                                                    this@RecentChatFragment
                                                )
                                            if (recentChatUsersList.isEmpty()) {
                                                binding.noChatTextView.visibility = View.VISIBLE
                                            }
                                        }
                                }
                            }
                        }
                    }
                } else {
                    binding.noChatTextView.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                loadingSpinnerOverlay.hideLoading()
                Log.e(TAG, "loadRecentChatUsers: " + error.message)
            }
        })
    }
}