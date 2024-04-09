package com.example.trialproject3.Fragment

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.trialproject3.Activity.LoginActivity
import com.example.trialproject3.Activity.MainActivity.OnBackPressedListener
import com.example.trialproject3.Activity.RegisterSellerStoreActivity
import com.example.trialproject3.Activity.ToCancelActivity
import com.example.trialproject3.Activity.ToReceiveActivity
import com.example.trialproject3.Activity.ToShipActivity
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Helper.AlertDialogHelper
import com.example.trialproject3.Map.MapboxMapActivity
import com.example.trialproject3.Models.StoreDetailsModel
import com.example.trialproject3.R
import com.example.trialproject3.databinding.FragmentProfileBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.toObject

class ProfileFragment : Fragment(), OnBackPressedListener {
    private val TAG = "ProfileFragment"
    private var context: Context? = null
    private lateinit var binding: FragmentProfileBinding
    private lateinit var intent: Intent
    private lateinit var alertDialogHelper: AlertDialogHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        context = requireContext()
        alertDialogHelper = AlertDialogHelper(context)
        binding.storeInfoLayout.visibility = View.GONE
        binding.storeDisplayLayout.visibility = View.GONE
        binding.registerStoreLayout.visibility = View.GONE
        binding.toShipBtn.visibility = View.GONE
        binding.toPayBtn.visibility = View.GONE
        binding.toReceiveBtn.visibility = View.GONE

        binding.backBtn.setOnClickListener { v: View? -> backToHomeFragment() }


        binding.logoutBtn.setOnClickListener {
            alertDialogHelper.showAlertDialog(
                "Logout",
                "Are you sure you want to Logout?",
                "Cancel",
                { dialog: DialogInterface?, which: Int -> alertDialogHelper.dismissDialog() },
                "Logout"
            ) { dialog: DialogInterface?, which: Int -> logoutUser() }
        }
        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) {
            loadUserProfile()
        }
    }

    override fun onBackPressed(): Boolean {
        backToHomeFragment()
        return true
    }

    private fun logoutUser() {
        FirebaseHelper.signOutUser()
        intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun loadUserProfile() {
        if (FirebaseHelper.currentUser() != null) {
            FirebaseHelper.currentUserDetails().get()
                .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                    if (task.isSuccessful) {
                        val documentSnapshot = task.result
                        val getFirstName = documentSnapshot.getString("Fname")
                        val getUserType = documentSnapshot.getString("UserType")
                        binding.fullNameTextView.text = getFirstName
                        binding.userTypeTextView.text = getUserType
                        binding.emailTextView.text = FirebaseHelper.currentUser().email

                        if (getUserType == "Seller") {
                            binding.storeInfoLayout.visibility = View.VISIBLE
                            val storeReference = FirebaseHelper.getFireStoreInstance()
                                .collection(FirebaseHelper.KEY_COLLECTION_STORES)

                            storeReference.addSnapshotListener { value, error ->
                                if (error != null) {
                                    Log.e(TAG, "loadUserProfile: " + error.message)
                                    return@addSnapshotListener
                                }

                                if (value != null && !value.isEmpty) {
                                    val storeDetailsModelList =
                                        mutableListOf<StoreDetailsModel>()
                                    for (stores in value.documents) {
                                        val storeDetailsModel =
                                            stores.toObject(StoreDetailsModel::class.java)
                                        if (storeDetailsModel != null) {
                                            if (storeDetailsModel.storeOwnerID == FirebaseHelper.currentUserID()) {
                                                binding.storeDisplayLayout.visibility = View.VISIBLE

                                                binding.storeNameTextView.text =
                                                    storeDetailsModel.storeName
                                                binding.storeLocationTextView.text =
                                                    storeDetailsModel.storeLocation
                                            } else {
                                                binding.registerStoreLayout.visibility = View.VISIBLE
                                                binding.registerStoreBtn.setOnClickListener {
                                                    val intent =
                                                        Intent(
                                                            context,
                                                            MapboxMapActivity::class.java
                                                        )
                                                    startActivity(intent)
                                                    requireActivity().finish()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }else{
                            binding.toShipBtn.visibility = View.VISIBLE
                            binding.toPayBtn.visibility = View.VISIBLE
                            binding.toReceiveBtn.visibility = View.VISIBLE

                            binding.toShipBtn.setOnClickListener { v: View? ->
//             Retrieve the cart items from the intent
//            List<PopularDomain> cartItems = (List<PopularDomain>)  getIntent().getSerializableExtra("cartItems");

//             Start ToShipActivity and pass the cart items
                                val intent = Intent(context, ToShipActivity::class.java)
                                //            intent.putExtra("cartItems", (Serializable) cartItems);
                                startActivity(intent)
                                requireActivity().finish()
                            }
                            binding.toPayBtn.setOnClickListener {
                                intent = Intent(context, ToShipActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                            binding.toReceiveBtn.setOnClickListener {
                                intent = Intent(context, ToReceiveActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                        }
                    } else {
                        Log.e(TAG, "loadUserProfile: " + task.exception)
                    }
                }
        }
    }

    private fun backToHomeFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, HomeFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}