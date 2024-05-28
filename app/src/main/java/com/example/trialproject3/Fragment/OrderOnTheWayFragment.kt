package com.example.trialproject3.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.trialproject3.Activity.MainActivity
import com.example.trialproject3.R
import com.example.trialproject3.databinding.FragmentOrderOnTheWayBinding


class OrderOnTheWayFragment : Fragment(), MainActivity.OnBackPressedListener {
    private lateinit var binding: FragmentOrderOnTheWayBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderOnTheWayBinding.inflate(inflater, container, false)

        binding.backToHomeBtn.setOnClickListener { backToHomeFragment() }

        return binding.root
    }

    override fun onBackPressed(): Boolean {
        backToHomeFragment()
        return true
    }

    private fun backToHomeFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, HomeFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}