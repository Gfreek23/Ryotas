package com.example.trialproject3.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.trialproject3.Models.PostsModel
import com.example.trialproject3.R
import com.example.trialproject3.databinding.FragmentMessageBinding

private const val POST_ID_PARAM = "postModel"
class MessageFragment : Fragment() {
    private lateinit var binding: FragmentMessageBinding
    companion object {

        @JvmStatic
        fun newInstance(postsModel: PostsModel) =
            MessageFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(POST_ID_PARAM, postsModel)
                }
            }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) {
            val postModel = arguments?.getSerializable(POST_ID_PARAM)
            if (postModel != null) {
                loadChatUserDetails(postModel as PostsModel)
            }
        }
    }

    private fun loadChatUserDetails(postsModel: PostsModel) {
        binding.chatNameTextView.text = postsModel.fullName

        if (postsModel.userPostImage != "none") {
            Glide.with(requireContext())
                .load(postsModel.userPostImage)
                .placeholder(R.drawable.ic_baseline_account_circle)
                .into(binding.chatUserProfilePicture)
        }
    }
}