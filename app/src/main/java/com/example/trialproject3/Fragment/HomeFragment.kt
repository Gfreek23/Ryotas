package com.example.trialproject3.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.trialproject3.Activity.LoginActivity
import com.example.trialproject3.Activity.MainActivity.OnBackPressedListener
import com.example.trialproject3.Adapter.ProductsAdapter
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Helper.AlertDialogHelper
import com.example.trialproject3.Models.ProductsModel
import com.example.trialproject3.R
import com.example.trialproject3.Utility.LoadingSpinnerOverlay
import com.example.trialproject3.Utility.ToastHelper
import com.example.trialproject3.databinding.FragmentHomeBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import java.util.ArrayList

class HomeFragment : Fragment(), OnBackPressedListener, ProductsAdapter.OnProductItemClickListener {
    private val TAG = "HomeFragment"
    private lateinit var binding: FragmentHomeBinding
    private lateinit var context: Context
    private lateinit var alertDialogHelper: AlertDialogHelper
    private lateinit var toastHelper: ToastHelper
    private lateinit var loadingSpinnerOverlay: LoadingSpinnerOverlay
    override fun onStart() {
        super.onStart()
        if (FirebaseHelper.currentUser() == null) {
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
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

        binding.noProductTextView.visibility = View.GONE
        binding.productsRecyclerView.layoutManager = GridLayoutManager(context, 2)

        binding.searchBar.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                showSearchFragment()
            }
            false
        }
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // This method is called to notify you that, within s, the count characters
                // beginning at start are about to be replaced by new text with length after.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // This method is called to notify you that, within s, the count characters
                // beginning at start have just replaced old text that had length before.
            }

            override fun afterTextChanged(s: Editable) {
                // This method is called to notify you that, somewhere within s, the text has been changed.
                if (s.toString().isEmpty())
                    loadProducts()

            }
        })

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadProducts()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) {
            fetchUserDetails()

            val productsList = arguments?.getSerializable("productsList") as? ArrayList<ProductsModel>
            Log.e(TAG, "productsList: " + productsList.toString())
            if (productsList != null) loadSearchedProducts(productsList)
            else loadProducts()
        }
    }

    override fun onBackPressed(): Boolean {
        exitApp()
        return true
    }

    override fun onProductItemClick(productsModel: ProductsModel) {

    }

    private fun showSearchFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, SearchFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun fetchUserDetails() {
        if (FirebaseHelper.currentUser() != null) {
            FirebaseHelper.currentUserDetails().get()
                .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                    if (task.isSuccessful) {
                        val documentSnapshot = task.result
                        val getFullName = documentSnapshot.getString("Fname")
                        val getUserType = documentSnapshot.getString("UserType")
                        binding.fullNameTextView.text = getFullName
                    } else
                        Log.e(TAG, "getUserDetails: " + task.exception)

                }
        }
    }

    private fun loadSearchedProducts(productsList: ArrayList<ProductsModel>) {
        loadingSpinnerOverlay.showLoading()
        val productsAdapter = ProductsAdapter(context, productsList, this@HomeFragment)

        if (productsList.isEmpty()) {
            binding.noProductTextView.visibility = View.VISIBLE
        }

        binding.productsRecyclerView.adapter = productsAdapter
        loadingSpinnerOverlay.hideLoading()
    }

    private fun loadProducts() {
        loadingSpinnerOverlay.showLoading()
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
                            productsList.add(product)
                        }
                    }
                    val productsAdapter = ProductsAdapter(context, productsList, this@HomeFragment)
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