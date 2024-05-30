package com.example.trialproject3.Activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.trialproject3.Adapter.MessageAdapter
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Models.MessageModel
import com.example.trialproject3.Models.PostsModel
import com.example.trialproject3.R
import com.example.trialproject3.databinding.ActivityMessageBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class MessageActivity : AppCompatActivity() {
    private val TAG = "MessageActivity"
    private lateinit var binding: ActivityMessageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("postModel")) {
            val postsModel = intent.getSerializableExtra("postModel") as PostsModel
            loadChatUserDetails(postsModel)
            displayMessages(postsModel)

            binding.sendMessageBtn.setOnClickListener {
                val message = binding.messageEditText.text.toString()
                if (message.isNotEmpty()) sendMessage(message, postsModel)
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

    private fun loadChatUserDetails(postsModel: PostsModel) {
        binding.chatNameTextView.text = postsModel.fullName

        if (postsModel.userPostImage != "none") {
            Glide.with(this)
                .load(postsModel.userPostImage)
                .placeholder(R.drawable.loading_gif)
                .into(binding.chatUserProfilePicture)
        }
    }

    private fun sendMessage(message: String, postsModel: PostsModel) {
        val messageModel = MessageModel(
            senderID = FirebaseHelper.currentUserID(),
            receiverID = postsModel.userID,
            message = message,
            messageTime = System.currentTimeMillis(),
            isRead = false
        )

        val database = Firebase.database
        val messageReference = database.getReference(FirebaseHelper.KEY_COLLECTION_MESSAGES)
        messageReference.push().setValue(messageModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.messageEditText.setText("")
                } else {
                    Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun displayMessages(postsModel: PostsModel) {
        val messagesModelList = ArrayList<MessageModel>()
        val messageAdapter = MessageAdapter(messagesModelList)
        binding.messagesRecyclerView.adapter = messageAdapter

        val database = Firebase.database
        val messageReference = database.getReference(FirebaseHelper.KEY_COLLECTION_MESSAGES)

        messageReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    messagesModelList.clear()
                    for (messageSnapshot in snapshot.children) {
                        val messageModel = messageSnapshot.getValue(MessageModel::class.java)
                        if (messageModel != null) {
                            if (messageModel.senderID == FirebaseHelper.currentUserID() && messageModel.receiverID == postsModel.userID ||
                                messageModel.senderID == postsModel.userID && messageModel.receiverID == FirebaseHelper.currentUserID()
                            ) {
                                messagesModelList.add(messageModel)
                            }
                        }
                    }
                    messageAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MessageActivity, "Failed to load messages", Toast.LENGTH_SHORT)
                    .show()
            }
        })


    }
}