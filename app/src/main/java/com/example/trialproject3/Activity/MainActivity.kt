package com.example.trialproject3.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Fragment.AddProductFragment
import com.example.trialproject3.Fragment.CartFragment
import com.example.trialproject3.Fragment.HomeFragment
import com.example.trialproject3.Fragment.PostContentFragment
import com.example.trialproject3.Fragment.PostsFragment
import com.example.trialproject3.Fragment.ProfileFragment
import com.example.trialproject3.Fragment.RecentChatFragment
import com.example.trialproject3.Map.MapboxMapActivity
import com.example.trialproject3.Models.StoreDetailsModel
import com.example.trialproject3.R
import com.example.trialproject3.databinding.ActivityMainBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    companion object {
        var fullName: String? = null
        var userType: String? = null
        var profilePicture: String? = "none"
        var phoneNumber: String? = null
        var storeName: String? = null
        var storeLocation: String? = null
    }

    interface OnBackPressedListener {
        fun onBackPressed(): Boolean
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.postContentFAB.visibility = View.GONE
        binding.addProductFAB.visibility = View.GONE

        initializeBottomNavBar()
        fetchUserDetails()

        if (intent.hasExtra("isAddedToCart")) showFragment(CartFragment())
        else showFragment(HomeFragment())

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.fragmentContainer)
                if (currentFragment is OnBackPressedListener) {
                    val handled = (currentFragment as OnBackPressedListener).onBackPressed()
                    if (handled) return
                }
            }
        })
    }

    private fun fetchUserDetails() {
        if (FirebaseHelper.currentUser() != null) {
            FirebaseHelper.currentUserDetails().get()
                .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                    if (task.isSuccessful) {
                        val documentSnapshot = task.result
                        val getFullName = documentSnapshot.getString("Fname")
                        val getUserType = documentSnapshot.getString("UserType")
                        val getProfilePicture = documentSnapshot.getString("ProfilePicture")
                        val getPhoneNumber = documentSnapshot.getString("Phone")

                        val sharedPreferences =
                            getSharedPreferences("currentUserPrefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("fullName", getFullName)
                        editor.putString("userType", getUserType)
                        editor.putString("profilePicture", getProfilePicture)
                        editor.putString("phoneNumber", getPhoneNumber)
                        editor.apply()

                        fullName = getFullName
                        userType = getUserType
                        profilePicture = getProfilePicture
                        phoneNumber = getPhoneNumber

                        if (getUserType == "Seller") {
                            val menu = binding.bottomNavBar.menu
                            val menuItem1 = menu.getItem(1)
                            val menuItem2 = menu.getItem(3)
                            //                                menuItem1.setVisible(false);
                            menuItem2.isVisible = false
                            binding.postContentFAB.visibility = View.VISIBLE
                            binding.addProductFAB.visibility = View.VISIBLE
                            binding.postContentFAB.setOnClickListener {
                                showFragment(
                                    PostContentFragment()
                                )
                            }
                            binding.addProductFAB.setOnClickListener {
                                showFragment(
                                    AddProductFragment()
                                )
                            }
                        }
                    } else {
                        Log.e(TAG, "getUserDetails: " + task.exception)
                    }
                }
            FirebaseHelper.getFireStoreInstance()
                .collection(FirebaseHelper.KEY_COLLECTION_STORES)
                .addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                    if (error != null) {
                        Log.e(TAG, "loadUserProfile: " + error.message)
                        return@addSnapshotListener
                    }
                    if (value != null && !value.isEmpty) {
                        val storeDetailsModelList = ArrayList<StoreDetailsModel>()
                        for (storeSnapshot in value.getDocuments()) {
                            val storeDetailsModel = storeSnapshot.toObject(
                                StoreDetailsModel::class.java
                            )
                            if (storeDetailsModel != null) {
                                storeDetailsModelList.add(storeDetailsModel)
                                if (storeDetailsModel.storeOwnerID == FirebaseHelper.currentUserID()) {
                                    storeName = storeDetailsModel.storeName
                                    storeLocation = storeDetailsModel.storeLocation

//                                        Toast.makeText(this, storeName, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
        }

    }

    private fun showFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    private fun initializeBottomNavBar() {
        binding.bottomNavBar.selectedItemId = R.id.navShop
        binding.bottomNavBar.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.navShop -> {
                    showFragment(HomeFragment())
                }

                R.id.navExplore -> {
                    val intent = Intent(this@MainActivity, MapboxMapActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                R.id.navPosts -> {
                    showFragment(PostsFragment())
                }

                R.id.navCart -> {
                    showFragment(CartFragment())
                }

                R.id.navChat -> {
                    showFragment(RecentChatFragment())
                }
            }
            true
        }
    }


}