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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trialproject3.Activity.LoginActivity
import com.example.trialproject3.Activity.MainActivity
import com.example.trialproject3.Activity.MainActivity.OnBackPressedListener
import com.example.trialproject3.Adapter.ProductsAdapter
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Helper.AlertDialogHelper
import com.example.trialproject3.Models.CartModel
import com.example.trialproject3.Models.ProductsModel
import com.example.trialproject3.Utility.ToastHelper
import com.example.trialproject3.databinding.FragmentHomeBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.toObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class HomeFragment : Fragment(), OnBackPressedListener, ProductsAdapter.OnProductItemClickListener {
    private val TAG = "HomeFragment"
    private lateinit var binding: FragmentHomeBinding
    private lateinit var context: Context
    private lateinit var alertDialogHelper: AlertDialogHelper
    private lateinit var toastHelper: ToastHelper
    override fun onStart() {
        super.onStart()
        if (FirebaseHelper.currentUser() == null) {
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        context = requireContext()
        alertDialogHelper = AlertDialogHelper(context)
        toastHelper = ToastHelper(context)

        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) {
            getUserDetails()
            loadProducts()
        }
    }

    override fun onBackPressed(): Boolean {
        exitApp()
        return true
    }

    override fun onProductItemClick(productsModel: ProductsModel) {
        addToCart(productsModel , UUID.randomUUID().toString())
    }

    private fun addToCart(productsModel: ProductsModel, cartID : String) {
        val cartReference = FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_CARTS)
            .document(cartID)

        val currentTimeMillis = System.currentTimeMillis()
        val sdf = SimpleDateFormat("MMMM-yyyy-dd hh:mm:ss a", Locale.getDefault())
        val currentDate = Date(currentTimeMillis)
        val formattedDateTime = sdf.format(currentDate)

        val cartModel = CartModel(
            cartID = cartID,
            cartUserID = FirebaseHelper.currentUserID(),
            productName = productsModel.productName,
            productDescription = productsModel.productDescription,
            price = productsModel.price,
            productImage = productsModel.productImage,
            quantity = 1,
            storeName = productsModel.storeName,
            storeLocation = productsModel.storeLocation,
            timeAdded = formattedDateTime,
        )
        cartReference.set(cartModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    toastHelper.showToast("Added to cart", 1)
                } else {
                    Log.e(TAG, "addToCart: " + task.exception)
                }
            }
    }

    private fun getUserDetails() {
        if (FirebaseHelper.currentUser() != null) {
            FirebaseHelper.currentUserDetails().get()
                .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                    if (task.isSuccessful) {
                        val documentSnapshot = task.result
                        val getFullName = documentSnapshot.getString("Fname")
                        val getUserType = documentSnapshot.getString("UserType")
                        binding.fullNameTextView.text = getFullName
                    } else {
                        Log.e(TAG, "getUserDetails: " + task.exception)
                    }
                }
        }
    }

    private fun loadProducts() {
        val productReference = FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_PRODUCTS)

        productReference.addSnapshotListener { value, error ->
            if (error != null) {
                Log.e(TAG, "loadProducts: " + error.message)
                return@addSnapshotListener
            }

            if (value != null && !value.isEmpty) {
                val productsList = mutableListOf<ProductsModel>()
                for (products in value.documents) {
                    val product = products.toObject(ProductsModel::class.java)
                    if (product != null) {
                        productsList.add(product)
                    }
                }
                val productsAdapter = ProductsAdapter(context, productsList, this@HomeFragment)
                binding.productsRecyclerView.layoutManager = LinearLayoutManager(context)
                binding.productsRecyclerView.adapter = productsAdapter
            }
        }
    }

    private fun exitApp() {
        alertDialogHelper.showAlertDialog(
            "Exit App", "Are you sure you want to Exit App?",
            "Exit", { dialog: DialogInterface?, which: Int -> requireActivity().finish() },
            "Cancel"
        ) { dialog: DialogInterface?, which: Int -> alertDialogHelper!!.dismissDialog() }
    }


}