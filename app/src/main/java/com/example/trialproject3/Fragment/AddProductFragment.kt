package com.example.trialproject3.Fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.trialproject3.Activity.MainActivity
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Models.ProductsModel
import com.example.trialproject3.R
import com.example.trialproject3.Utility.LoadingSpinnerOverlay
import com.example.trialproject3.Utility.ToastHelper
import com.example.trialproject3.databinding.FragmentAddProductBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


class AddProductFragment : Fragment(), MainActivity.OnBackPressedListener {
    private val TAG = "AddProductFragment"
    private lateinit var binding: FragmentAddProductBinding
    private var productImageUri: Uri? = null
    private lateinit var productImage: String
    private lateinit var toastHelper: ToastHelper
    private lateinit var loadingSpinnerOverlay: LoadingSpinnerOverlay
    private lateinit var context: Context
    private var selectedCategory: String? = null

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddProductFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddProductBinding.inflate(inflater, container, false)

        binding.progressBar.visibility = View.GONE
        context = requireContext()
        toastHelper = ToastHelper(context)
        loadingSpinnerOverlay = LoadingSpinnerOverlay(context)

        binding.productImageView.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        val productCategorySpinner = binding.productCategorySpinner
        val postCategories = resources.getStringArray(R.array.product_categories)
        val adapter = object : ArrayAdapter<String>(
            context,
            R.layout.spinner_item,
            postCategories
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }
        }
        adapter.setDropDownViewResource(R.layout.spinner_item)
        productCategorySpinner.adapter = adapter

        productCategorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    selectedCategory = parent.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    selectedCategory = null
                }
            }

        binding.addBtn.setOnClickListener { addProduct() }

        return binding.root
    }

    override fun onBackPressed(): Boolean {
        goToFragment(HomeFragment())
        return true
    }

    private fun addProduct() {
        val productName = binding.productNameEditText.text.toString()
        val productDescription = binding.descriptionEditText.text.toString()
        val price = binding.priceEditText.text.toString().toDouble()
        val stock = binding.stockEditText.text.toString().toInt()

        if (productName.isEmpty()) {
            binding.productNameEditText.requestFocus()
            binding.productNameEditText.error = "Enter Product name"
        } else if (productDescription.isEmpty()) {
            binding.descriptionEditText.requestFocus()
            binding.descriptionEditText.error = "Enter Product description"
        } else if (price == 0.0) {
            binding.priceEditText.requestFocus()
            binding.priceEditText.error = "Enter price"
        } else if (stock == 0) {
            binding.stockEditText.requestFocus()
            binding.stockEditText.error = "Enter stock"
        } else if (productImageUri == null) {
            toastHelper.showToast("Enter product image", 1)
        } else if (selectedCategory == null || selectedCategory == "Select product category") {
            binding.productCategorySpinner.requestFocus()
            toastHelper.showToast("Select product category", 1)
        } else if (MainActivity.storeLocation == null || MainActivity.storeName == null) {
            toastHelper.showToast("Your Store is not registered", 1)
        } else {
            loadingSpinnerOverlay.showLoading()
            binding.progressBar.visibility = View.VISIBLE
            binding.addBtn.visibility = View.GONE

            val currentTimeMillis = System.currentTimeMillis()
            val sdf = SimpleDateFormat("MMMM-yyyy-dd hh:mm:ss a", Locale.getDefault())
            val currentDate = Date(currentTimeMillis)
            val formattedDateTime = sdf.format(currentDate)

            if (productImageUri != null) {
                val storageRef = FirebaseStorage.getInstance().reference
                val productImageRef = storageRef.child("/productsImages/${UUID.randomUUID()}")

                productImageRef.putFile(productImageUri!!)
                    .addOnCompleteListener { uploadTask ->
                        if (uploadTask.isSuccessful) {
                            productImageRef.downloadUrl
                                .addOnSuccessListener {
                                    productImage = it.toString()

                                    val sharedPreferences = context.getSharedPreferences(
                                        "currentUserPrefs",
                                        Context.MODE_PRIVATE
                                    )

                                    val fullName = sharedPreferences.getString("fullName", null)
                                    val userType = sharedPreferences.getString("userType", null)
                                    val profilePicture =
                                        sharedPreferences.getString("profilePicture", null)
                                    val phoneNumber =
                                        sharedPreferences.getString("phoneNumber", null)

                                    val product = ProductsModel(
                                        productID = UUID.randomUUID().toString(),
                                        sellerUserID = FirebaseHelper.currentUserID(),
                                        userType = userType!!,
                                        sellerName = fullName!!,
                                        sellerProfilePicture = profilePicture!!,
                                        sellerEmail = FirebaseHelper.currentUser().email.toString(),
                                        sellerPhoneNumber = phoneNumber!!,
                                        productName = productName,
                                        productNameLowercase = productName.toLowerCase(Locale.getDefault()),
                                        productNameWords = productName.toLowerCase(Locale.getDefault())
                                            .split(" "),
                                        productRatings = 0.0F,
                                        productDescription = productDescription,
                                        productCategory = selectedCategory!!,
                                        price = price,
                                        stock = stock,
                                        productImage = productImage,
                                        storeName = MainActivity.storeName!!,
                                        storeLocation = MainActivity.storeLocation!!,
                                        timePosted = formattedDateTime
                                    )

                                    FirebaseHelper.getFireStoreInstance()
                                        .collection(FirebaseHelper.KEY_COLLECTION_PRODUCTS)
                                        .document(product.productID)
                                        .set(product)
                                        .addOnCompleteListener { task ->
                                            loadingSpinnerOverlay.hideLoading()
                                            binding.progressBar.visibility = View.GONE
                                            binding.addBtn.visibility = View.VISIBLE

                                            if (task.isSuccessful) {
                                                binding.productNameEditText.setText("")
                                                binding.descriptionEditText.setText("")
                                                goToFragment(HomeFragment())
                                                toastHelper.showToast("Product added", 0)
                                            } else {
                                                toastHelper.showToast("Product failed to add", 1)
                                                Log.e(TAG, "addProduct: ${task.result}")
                                            }
                                        }
                                }
                                .addOnFailureListener {
                                    loadingSpinnerOverlay.hideLoading()
                                    binding.progressBar.visibility = View.GONE
                                    binding.addBtn.visibility = View.VISIBLE
                                    Log.e(TAG, "addProduct: " + it.message)
                                }
                        } else {
                            Log.e(TAG, "addProduct: " + uploadTask.exception)
                        }
                    }
            }
        }
    }

    private fun goToFragment(fragment: Fragment) {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                productImageUri = data.data
                binding.productImageView.setImageURI(productImageUri)
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            toastHelper.showToast(ImagePicker.getError(data), 1)
        } else {
            toastHelper.showToast("Task Cancelled", 1)
        }
    }
}