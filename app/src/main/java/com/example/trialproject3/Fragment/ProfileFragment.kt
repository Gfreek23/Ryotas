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
import com.example.trialproject3.R
import com.example.trialproject3.databinding.FragmentProfileBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

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

        binding.backBtn.setOnClickListener { v: View? -> backToHomeFragment() }
        binding.toShipBtn.setOnClickListener { v: View? ->
//             Retrieve the cart items from the intent
//            List<PopularDomain> cartItems = (List<PopularDomain>)  getIntent().getSerializableExtra("cartItems");

//             Start ToShipActivity and pass the cart items
            val intent = Intent(context, ToShipActivity::class.java)
            //            intent.putExtra("cartItems", (Serializable) cartItems);
            startActivity(intent)
            requireActivity().finish()
        }
        binding.toPayBtn.setOnClickListener { v: View? ->
            intent = Intent(context, ToShipActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        binding.toReceiveBtn.setOnClickListener { v: View? ->
            intent = Intent(context, ToReceiveActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        binding.cancelBtn.setOnClickListener { v: View? ->
            intent = Intent(context, ToCancelActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        binding.logoutBtn.setOnClickListener { v: View? ->
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
                            if (!documentSnapshot.contains("StoreName")) {
                                binding.registerStoreBtn.setOnClickListener {
                                    val intent =
                                        Intent(context, MapboxMapActivity::class.java)
                                    startActivity(intent)
                                    requireActivity().finish()
                                }
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