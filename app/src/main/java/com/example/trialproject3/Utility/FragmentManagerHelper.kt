package com.example.trialproject3.Utility

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.trialproject3.R

class FragmentManagerHelper(private val activity: FragmentActivity) {
    fun showFragment(fragment: Fragment) {
        val fragmentManager = activity.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }
}