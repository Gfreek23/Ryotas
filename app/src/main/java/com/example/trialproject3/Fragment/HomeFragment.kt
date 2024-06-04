package com.example.trialproject3.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.trialproject3.Activity.MainActivity.OnBackPressedListener
import com.example.trialproject3.Adapter.ProductsAdapter
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Helper.AlertDialogHelper
import com.example.trialproject3.Models.ProductsModel
import com.example.trialproject3.R
import com.example.trialproject3.Utility.FragmentManagerHelper
import com.example.trialproject3.Utility.LoadingSpinnerOverlay
import com.example.trialproject3.Utility.ToastHelper
import com.example.trialproject3.databinding.FragmentHomeBinding

class HomeFragment : Fragment(),
    OnBackPressedListener {
    private val TAG = "HomeFragment"
    private lateinit var binding: FragmentHomeBinding
    private lateinit var context: Context
    private lateinit var alertDialogHelper: AlertDialogHelper
    private lateinit var toastHelper: ToastHelper
    private lateinit var loadingSpinnerOverlay: LoadingSpinnerOverlay
    private lateinit var fragmentManagerHelper: FragmentManagerHelper

    override fun onDestroy() {
        super.onDestroy()
        if (loadingSpinnerOverlay.isShowing) {
            loadingSpinnerOverlay.dismiss()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        context = requireContext()
        alertDialogHelper = AlertDialogHelper(context)
        toastHelper = ToastHelper(context)
        loadingSpinnerOverlay = LoadingSpinnerOverlay(context)
        fragmentManagerHelper = FragmentManagerHelper(activity as FragmentActivity)

        binding.noProductTextView.visibility = View.GONE
        binding.productsRecyclerView.layoutManager = GridLayoutManager(context, 2)

        val sharedPreferences =
            context.getSharedPreferences("currentUserPrefs", Context.MODE_PRIVATE)
        val fullName = sharedPreferences.getString("fullName", null)
        val profilePicture = sharedPreferences.getString("profilePicture", null)
        binding.fullNameTextView.text = fullName

        if (profilePicture != "none") {
            Glide.with(context)
                .load(profilePicture)
                .placeholder(R.drawable.loading_gif)
                .into(binding.userProfilePicture)
        }

        binding.upperLayout.setOnClickListener { fragmentManagerHelper.showFragment(ProfileFragment()) }

        binding.searchBar.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                fragmentManagerHelper.showFragment(SearchProductsFragment())
            }
            false
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadProducts()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) {
            val productsList =
                arguments?.getSerializable("productsList") as? ArrayList<ProductsModel>
            Log.e(TAG, "productsList: " + productsList.toString())
            if (productsList != null) loadSearchedProducts(productsList)
            else loadProducts()
        }
    }

    override fun onBackPressed(): Boolean {
        exitApp()
        return true
    }

    private fun loadSearchedProducts(productsList: ArrayList<ProductsModel>) {
        loadingSpinnerOverlay.showLoading()
        val productsAdapter = ProductsAdapter(context, productsList)
        binding.productsRecyclerView.adapter = productsAdapter

        if (productsList.isEmpty()) {
            binding.productErrorTextView.visibility = View.GONE
            binding.noProductTextView.visibility = View.VISIBLE
        } else {
            binding.productErrorTextView.visibility = View.GONE
            binding.noProductTextView.visibility = View.GONE
        }

        loadingSpinnerOverlay.hideLoading()
    }

    private fun loadProducts() {
        loadingSpinnerOverlay.showLoading()
        binding.noProductTextView.visibility = View.GONE
        val productReference = FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_PRODUCTS)

        productReference
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e(TAG, "loadProducts: " + error.message)
                    loadingSpinnerOverlay.hideLoading()
                    return@addSnapshotListener
                }

                if (value != null && !value.isEmpty) {
                    val productsList = mutableListOf<ProductsModel>()
                    for (products in value.documents) {
                        val product = products.toObject(ProductsModel::class.java)
                        if (product != null) {
                            binding.productErrorTextView.visibility = View.GONE
                            productsList.add(product)
                        }
                    }
                    val productsAdapter = ProductsAdapter(context, productsList)
                    binding.productsRecyclerView.adapter = productsAdapter
                }
                loadingSpinnerOverlay.hideLoading()
            }
    }

    private fun exitApp() {
        alertDialogHelper.showAlertDialog(
            "Exit App", "Are you sure you want to Exit App?",
            "Exit", { dialog: DialogInterface?, which: Int -> requireActivity().finish() },
            "Cancel"
        ) { dialog: DialogInterface?, which: Int -> alertDialogHelper.dismissDialog() }
    }


}