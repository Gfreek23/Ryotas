package com.example.trialproject3.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RatingBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Models.CartModel
import com.example.trialproject3.Models.ProductsModel
import com.example.trialproject3.Models.RecentChatUserModel
import com.example.trialproject3.Utility.LoadingSpinnerOverlay
import com.example.trialproject3.Utility.ToastHelper
import com.example.trialproject3.databinding.ActivityProductDetailsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ProductDetailsActivity : AppCompatActivity() {
    private val TAG = "ProductDetailsActivity"
    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var toastHelper: ToastHelper
    private lateinit var loadingSpinnerOverlay: LoadingSpinnerOverlay
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.checkBtn.visibility = View.GONE
        binding.progressBar.visibility = View.GONE

        toastHelper = ToastHelper(this@ProductDetailsActivity)
        loadingSpinnerOverlay = LoadingSpinnerOverlay(this@ProductDetailsActivity)

        binding.backArrow.setOnClickListener { finish() }

        if (intent.hasExtra("productDetails")) {
            val productsModel = intent.getSerializableExtra("productDetails") as? ProductsModel
            if (productsModel != null) {
                Log.d(TAG, "productDetails: $productsModel")
                loadProductDetails(productsModel)
                fetchSellerDetails(productsModel.sellerUserID)

                binding.addToCartBtn.setOnClickListener {
                    addToCart(
                        productsModel,
                        UUID.randomUUID().toString()
                    )
                }

                binding.productRatings.onRatingBarChangeListener =
                    RatingBar.OnRatingBarChangeListener { _, rating, _ ->
                        if (rating >= 0.5) {
                            binding.checkBtn.visibility = View.VISIBLE
                            binding.checkBtn.setOnClickListener {
                                rateProduct(
                                    productsModel.productID,
                                    rating
                                )
                            }
                        } else binding.checkBtn.visibility = View.GONE
                    }

            } else {
                Log.e(TAG, "Failed to cast productDetails to ProductsModel")
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

    }

    @SuppressLint("SetTextI18n")
    private fun loadProductDetails(productsModel: ProductsModel) {
        loadingSpinnerOverlay.showLoading()

        val sharedPreferences = getSharedPreferences("currentUserPrefs", MODE_PRIVATE)
        val userType = sharedPreferences.getString("userType", null)

        if (userType == "Seller" && productsModel.sellerUserID == FirebaseHelper.currentUserID()) {
            binding.addToCartBtn.visibility = View.GONE
            binding.productRatings.setIsIndicator(true)
        }

        binding.productNameTextView.text = productsModel.productName
        binding.priceTextView.text = "₱ ${productsModel.price}"
        binding.productRatings.rating = productsModel.productRatings
        binding.productDescriptionTextView.text = productsModel.productDescription
        binding.productCategoryTextView.text = productsModel.productCategory
        binding.storeNameTextView.text = productsModel.storeName

        if (productsModel.productImage != "none") {
            Glide.with(this)
                .load(productsModel.productImage)
                .into(binding.productImageView)
        }
        loadingSpinnerOverlay.hideLoading()
    }

    private fun fetchSellerDetails(sellerUserID: String) {
        FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_USERS)
            .document(sellerUserID)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val sellerDocument = task.result
                    val recentChatUserModel = RecentChatUserModel(
                        chatUserID = sellerUserID,
                        chatUserName = sellerDocument?.getString("Fname") ?: "",
                        chatUserProfilePicture = sellerDocument.getString("ProfilePicture")
                            ?: "none"
                    )

                    binding.chatBtn.setOnClickListener {
                        val intent =
                            Intent(this@ProductDetailsActivity, MessageActivity::class.java)
                        intent.putExtra("chatUserData", recentChatUserModel)
                        startActivity(intent)
                    }
                }
            }


    }

    private fun rateProduct(productID: String, rating: Float) {
        binding.progressBar.visibility = View.VISIBLE
        binding.checkBtn.visibility = View.GONE

        val productReference = FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_PRODUCTS)
            .document(productID)

        productReference.update("productRatings", rating)
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE

                if (task.isSuccessful) {
                    toastHelper.showToast("Rated $rating", 1)
                    binding.productRatings.setIsIndicator(true)

                } else {
                    Log.e(TAG, "rateProduct: " + task.exception)
                    binding.checkBtn.visibility = View.VISIBLE

                }
            }
    }

    private fun addToCart(productsModel: ProductsModel, cartID: String) {
        loadingSpinnerOverlay.showLoading()
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
            productNameLowercase = productsModel.productName.toLowerCase(Locale.getDefault()),
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
                loadingSpinnerOverlay.hideLoading()
                if (task.isSuccessful) {
                    val intent = Intent(this@ProductDetailsActivity, MainActivity::class.java)
                    intent.putExtra("isAddedToCart", true)
                    startActivity(intent)
                    finish()
                    toastHelper.showToast("Added to cart", 1)
                } else
                    Log.e(TAG, "addToCart: " + task.exception)

            }
    }

}