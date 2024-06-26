package com.example.trialproject3.Activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.trialproject3.Adapter.MessageAdapter
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Models.MessageModel
import com.example.trialproject3.Models.RecentChatUserModel
import com.example.trialproject3.R
import com.example.trialproject3.Utility.LoadingSpinnerOverlay
import com.example.trialproject3.databinding.ActivityMessageBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class MessageActivity : AppCompatActivity() {
    private val TAG = "MessageActivity"
    private lateinit var binding: ActivityMessageBinding
    private lateinit var loadingSpinnerOverlay: LoadingSpinnerOverlay

    override fun onDestroy() {
        super.onDestroy()
        if (loadingSpinnerOverlay.isShowing) {
            loadingSpinnerOverlay.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingSpinnerOverlay = LoadingSpinnerOverlay(this)
        binding.sendingMessageProgressBar.visibility = View.GONE
        binding.startConversationTextView.visibility = View.GONE

        if (intent.hasExtra("chatUserData")) {
            val recentChatUserModel =
                intent.getSerializableExtra("chatUserData") as RecentChatUserModel
            loadChatUserDetails(recentChatUserModel)
            displayMessages(recentChatUserModel)

            binding.sendMessageBtn.setOnClickListener {
                val message = binding.messageEditText.text.toString()
                if (message.isNotEmpty()) sendMessage(message, recentChatUserModel)
            }
        }

        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.messageEditText.requestFocus()

        binding.backBtn.setOnClickListener { finish() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun loadChatUserDetails(recentChatUserModel: RecentChatUserModel) {
        binding.chatNameTextView.text = recentChatUserModel.chatUserName

        if (recentChatUserModel.chatUserProfilePicture != "none") {
            Glide.with(this)
                .load(recentChatUserModel.chatUserProfilePicture)
                .placeholder(R.drawable.loading_gif)
                .into(binding.chatUserProfilePicture)
        }
    }

    private fun sendMessage(message: String, recentChatUserModel: RecentChatUserModel) {
        binding.sendingMessageProgressBar.visibility = View.VISIBLE
        val messageModel = MessageModel(
            senderID = FirebaseHelper.currentUserID(),
            receiverID = recentChatUserModel.chatUserID,
            message = message,
            messageTime = System.currentTimeMillis(),
            isRead = false
        )

        val database = Firebase.database
        val messageReference = database.getReference(FirebaseHelper.KEY_COLLECTION_MESSAGES)
        messageReference.push().setValue(messageModel)
            .addOnCompleteListener { task ->
                binding.sendingMessageProgressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    binding.startConversationTextView.visibility = View.GONE
                    binding.messageEditText.setText("")
                } else {
                    Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "sendMessage: " + task.exception)
                }
            }
    }

    private fun displayMessages(recentChatUserModel: RecentChatUserModel) {
        binding.loadingGif.visibility = View.VISIBLE
        val messagesModelList = ArrayList<MessageModel>()
        val messageAdapter = MessageAdapter(
            this,
            messagesModelList,
            FirebaseHelper.currentUserID(),
            recentChatUserModel.chatUserProfilePicture
        )
        binding.messagesRecyclerView.adapter = messageAdapter

        val database = Firebase.database
        val messageReference = database.getReference(FirebaseHelper.KEY_COLLECTION_MESSAGES)

        messageReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.loadingGif.visibility = View.GONE
                if (snapshot.exists()) {
                    messagesModelList.clear()
                    for (messageSnapshot in snapshot.children) {
                        val messageModel = messageSnapshot.getValue(MessageModel::class.java)
                        if (messageModel != null) {
                            if (messageModel.senderID == FirebaseHelper.currentUserID() &&
                                messageModel.receiverID == recentChatUserModel.chatUserID ||
                                messageModel.senderID == recentChatUserModel.chatUserID &&
                                messageModel.receiverID == FirebaseHelper.currentUserID()
                            ) {
                                messagesModelList.add(messageModel)
                                binding.startConversationTextView.visibility = View.GONE
                            }else {
                                binding.startConversationTextView.visibility = View.VISIBLE
                                binding.startConversationTextView.text =
                                    "Start a conversation with ${recentChatUserModel.chatUserName}"
                            }
                        }
                    }
                    messageAdapter.notifyDataSetChanged()
                    messageAdapter.notifyItemInserted(messagesModelList.size - 1)
                    binding.messagesRecyclerView.scrollToPosition(messagesModelList.size - 1)
                } else {
                    binding.startConversationTextView.visibility = View.VISIBLE
                    binding.startConversationTextView.text =
                        "Start a conversation with ${recentChatUserModel.chatUserName}"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MessageActivity, "Failed to load messages", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}