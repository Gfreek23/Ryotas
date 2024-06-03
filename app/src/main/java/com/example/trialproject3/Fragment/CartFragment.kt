package com.example.trialproject3.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trialproject3.Activity.AddressActivity
import com.example.trialproject3.Activity.MainActivity.OnBackPressedListener
import com.example.trialproject3.Adapter.CartAdapter
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Helper.AlertDialogHelper
import com.example.trialproject3.Models.CartModel
import com.example.trialproject3.Utility.FragmentManagerHelper
import com.example.trialproject3.Utility.LoadingSpinnerOverlay
import com.example.trialproject3.Utility.ToastHelper
import com.example.trialproject3.databinding.FragmentCartBinding

class CartFragment : Fragment(),
    OnBackPressedListener,
    CartAdapter.OnIncreaseCartClickListener,
    CartAdapter.OnDecreaseCartClickListener,
    CartAdapter.OnRemoveCartItemClickListener {

    private lateinit var binding: FragmentCartBinding
    private lateinit var context: Context
    private lateinit var builder: AlertDialog.Builder
    private lateinit var toastHelper: ToastHelper
    private lateinit var cartAdapter: CartAdapter
    private lateinit var loadingSpinnerOverlay: LoadingSpinnerOverlay
    private lateinit var alertDialogHelper: AlertDialogHelper
    private lateinit var fragmentManagerHelper: FragmentManagerHelper
    private var selectedPaymentMethod: String? = null

    companion object {
        private const val TAG = "CartFragment"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        context = requireContext()
        builder = AlertDialog.Builder(context)
        toastHelper = ToastHelper(context)
        loadingSpinnerOverlay = LoadingSpinnerOverlay(context)
        alertDialogHelper = AlertDialogHelper(context)
        fragmentManagerHelper = FragmentManagerHelper(activity as FragmentActivity)

        binding.backBtn.setOnClickListener { fragmentManagerHelper.showFragment(HomeFragment()) }

        binding.paymentMethodRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = group.findViewById<RadioButton>(checkedId)
            selectedPaymentMethod = radioButton.text.toString()
            binding.paymentMethodTextView.text = "Payment Method: $selectedPaymentMethod"
        }

        binding.shopBtn.setOnClickListener { fragmentManagerHelper.showFragment(HomeFragment()) }

        binding.addAddressBtn.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    AddressActivity::class.java
                )
            )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) loadCart()
    }

    override fun onBackPressed(): Boolean {
        fragmentManagerHelper.showFragment(HomeFragment())
        return true
    }

    @SuppressLint("SetTextI18n")
    private fun loadCart() {
        loadingSpinnerOverlay.showLoading()
        val cartReference = FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_CARTS)

        cartReference.addSnapshotListener { value, error ->
            if (error != null) {
                Log.e(TAG, "loadCart: " + error.message)
                loadingSpinnerOverlay.hideLoading()
                return@addSnapshotListener
            }

            if (value != null && !value.isEmpty) {
                val cartsList = mutableListOf<CartModel>()
                for (carts in value.documents) {
                    val cartModel = carts.toObject(CartModel::class.java)
                    if (cartModel != null && cartModel.cartUserID == FirebaseHelper.currentUserID()) {
                        binding.emptyCartLayout.visibility = View.GONE
                        if (cartModel.cartUserID == FirebaseHelper.currentUserID()) {
                            cartsList.add(cartModel)

                            binding.orderNowBtn.setOnClickListener {
                                if (selectedPaymentMethod != null)
                                    builder.setTitle("Are you sure?")
                                        .setMessage("Do you want to purchase these items?")
                                        .setCancelable(true)
                                        .setPositiveButton("Yes") { dialog: DialogInterface?, which: Int ->
                                            decreaseStock(cartAdapter.cartModelList)
                                        }
                                        .setNegativeButton("No") { dialog: DialogInterface, which: Int -> dialog.cancel() }
                                        .show()
                                else toastHelper.showToast(
                                    "Please select a payment method",
                                    1
                                )

                            }
                        }
                    }
                }
                if (cartsList.isNotEmpty()) {
                    cartAdapter =
                        CartAdapter(
                            context,
                            cartsList,
                            this@CartFragment,
                            this@CartFragment,
                            this@CartFragment
                        )
                    binding.cartRecyclerView.layoutManager = LinearLayoutManager(context)
                    binding.cartRecyclerView.adapter = cartAdapter

                    val subtotal = cartAdapter.calculateTotalPrice()
                    binding.subTotalTextView.text = "Subtotal: ₱$subtotal"

                    val totalPrice = subtotal
                    binding.totalTextView.text = "Total Price: ₱$totalPrice"
                } else {
                    binding.cartLayout.visibility = View.GONE
                }
            } else {
                binding.cartLayout.visibility = View.GONE
            }
            loadingSpinnerOverlay.hideLoading()
        }
    }

    override fun onIncreaseCartClick(cartModel: CartModel) {
        val productReference = FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_PRODUCTS)
            .document(cartModel.productID)

        productReference.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val currentStock = document.getLong("stock") ?: 0
                    if (cartModel.quantity < currentStock) {
                        cartModel.quantity++
                        updateQuantity(cartModel)
                    } else {
                        toastHelper.showToast("Cannot add more items. Product is out of stock.", 1)
                    }
                } else {
                    Log.e(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to get document: ${exception.message}")
            }
    }

    override fun onDecreaseCartClick(cartModel: CartModel) {
        if (cartModel.quantity > 1) {
            cartModel.quantity--
            updateQuantity(cartModel)
        }
    }

    override fun onRemoveCartItemClick(cartModel: CartModel) {
        val cartReference = FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_CARTS)
            .document(cartModel.cartID)

        alertDialogHelper.showAlertDialog(
            "Cart",
            "Are you sure you want to remove this item from your cart?",
            "Remove",
            { dialog, id ->
                cartReference.delete()
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error deleting document", e)
                    }
            },
            "Close",
            { dialog, id ->
                dialog.dismiss()
            }
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateQuantity(cartModel: CartModel) {
        val cartReference = FirebaseHelper.getFireStoreInstance()
            .collection(FirebaseHelper.KEY_COLLECTION_CARTS)
            .document(cartModel.cartID)

        cartReference.update("quantity", cartModel.quantity)
            .addOnSuccessListener {
                if (cartAdapter.cartModelList.isEmpty()) {
                    binding.emptyCartLayout.visibility = View.VISIBLE
                } else {
                    binding.emptyCartLayout.visibility = View.GONE
                }
                cartAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to update quantity: ${e.message}")
            }
    }

    private fun decreaseStock(cartModels: List<CartModel>) {
        loadingSpinnerOverlay.showLoading()
        for (cartModel in cartModels) {
            val productReference = FirebaseHelper.getFireStoreInstance()
                .collection(FirebaseHelper.KEY_COLLECTION_PRODUCTS)
                .document(cartModel.productID)

            productReference.get().addOnSuccessListener { document ->
                if (document != null) {
                    val currentStock = document.getLong("stock") ?: 0
                    if (currentStock >= cartModel.quantity) {
                        val newStock = currentStock - cartModel.quantity
                        productReference.update("stock", newStock)
                            .addOnCompleteListener { task ->
                                loadingSpinnerOverlay.hideLoading()
                                if (task.isSuccessful) {
                                    fragmentManagerHelper.showFragment(OrderOnTheWayFragment())
                                    Log.d(TAG, "Stock decreased successfully for product: ${cartModel.productID}")
                                } else {
                                    Log.e(TAG, "Failed to decrease stock: ${task.exception?.message}")
                                }
                            }
                    } else {
                        Log.e(TAG, "Not enough stock available for product: ${cartModel.productID}")
                    }
                } else {
                    Log.e(TAG, "No such document for product: ${cartModel.productID}")
                }
            }.addOnFailureListener { exception ->
                Log.e(TAG, "Failed to get document: ${exception.message}")
            }
        }
    }}