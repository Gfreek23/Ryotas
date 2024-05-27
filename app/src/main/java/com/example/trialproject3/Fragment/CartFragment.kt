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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trialproject3.Activity.AddressActivity
import com.example.trialproject3.Activity.MainActivity.OnBackPressedListener
import com.example.trialproject3.Activity.ToShipActivity
import com.example.trialproject3.Adapter.CartAdapter
import com.example.trialproject3.Adapter.CartListAdapter
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Helper.AlertDialogHelper
import com.example.trialproject3.Helper.ManagementCart
import com.example.trialproject3.Models.CartModel
import com.example.trialproject3.R
import com.example.trialproject3.Utility.LoadingSpinnerOverlay
import com.example.trialproject3.Utility.ToastHelper
import com.example.trialproject3.databinding.FragmentCartBinding
import java.io.Serializable

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

    companion object {
        private const val TAG = "CartFragment"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        binding.emptyTxt.visibility = View.GONE

        context = requireContext()
        builder = AlertDialog.Builder(context)
        toastHelper = ToastHelper(context)
        loadingSpinnerOverlay = LoadingSpinnerOverlay(context)
        alertDialogHelper = AlertDialogHelper(context)

        binding.backBtn.setOnClickListener { backToHomeFragment() }


        binding.paymentMethodRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = group.findViewById<RadioButton>(checkedId)
            val selectedPaymentMethod = radioButton.text.toString()
            binding.paymentMethodTextView.text = "Payment Method: $selectedPaymentMethod"
            // Now you can use the selectedPaymentMethod
        }

        binding.orderNowBtn.setOnClickListener {
            builder.setTitle("Are you sure?")
                .setMessage("Do you want to purchase these items?")
                .setCancelable(true)
                .setPositiveButton("Yes") { dialog: DialogInterface?, which: Int ->
                }
                .setNegativeButton("No") { dialog: DialogInterface, which: Int -> dialog.cancel() }
                .show()
        }

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
        if (isAdded)
            loadCart()

    }

    override fun onBackPressed(): Boolean {
        backToHomeFragment()
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
                    val cart = carts.toObject(CartModel::class.java)
                    if (cart != null) {
                        if (cart.cartUserID == FirebaseHelper.currentUserID()) {
                            cartsList.add(cart)
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
                    binding.emptyTxt.visibility = View.VISIBLE
                }
            } else {
                binding.cartLayout.visibility = View.GONE
                binding.emptyTxt.visibility = View.VISIBLE
            }
            loadingSpinnerOverlay.hideLoading()
        }
    }

    private fun backToHomeFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, HomeFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onIncreaseCartClick(cartModel: CartModel) {
        cartModel.quantity++
        updateQuantity(cartModel)
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
                cartAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to update quantity: ${e.message}")
            }
    }
}