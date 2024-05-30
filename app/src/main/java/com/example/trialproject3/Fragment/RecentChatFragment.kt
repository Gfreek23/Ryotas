package com.example.trialproject3.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trialproject3.Adapter.ChatAdapter
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Models.RecentChatUsersModel
import com.example.trialproject3.databinding.FragmentRecentChatBinding

class RecentChatFragment : Fragment() {
    private lateinit var binding: FragmentRecentChatBinding

    companion object {

        @JvmStatic
        fun newInstance() =
            RecentChatFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecentChatBinding.inflate(inflater, container, false)

        binding.noChatTextView.visibility = View.GONE


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) loadRecentChatUsers()
    }

    private fun loadRecentChatUsers() {
        val recentChatUsersList = ArrayList<RecentChatUsersModel>()

        FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_USERS)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val recentChatUsersModel = RecentChatUsersModel(
                            userID = document.id,
                            userName = document.data["Fname"].toString(),
                            userImage = document.data["ProfilePicture"].toString(),
                        )
                        recentChatUsersList.add(recentChatUsersModel)
                    }
                    binding.recentChatUsersRecyclerView.layoutManager =
                        LinearLayoutManager(requireContext())
                    binding.recentChatUsersRecyclerView.adapter =
                        ChatAdapter(requireContext(), recentChatUsersList)
                    if (recentChatUsersList.isEmpty()) {
                        binding.noChatTextView.visibility = View.VISIBLE
                    }
                }
            }
    }
}