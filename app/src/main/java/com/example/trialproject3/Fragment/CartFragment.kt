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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trialproject3.Activity.AddressActivity
import com.example.trialproject3.Activity.MainActivity.OnBackPressedListener
import com.example.trialproject3.Activity.ToShipActivity
import com.example.trialproject3.Adapter.CartAdapter
import com.example.trialproject3.Adapter.CartListAdapter
import com.example.trialproject3.Firebase.FirebaseHelper
import com.example.trialproject3.Helper.ManagementCart
import com.example.trialproject3.Models.CartModel
import com.example.trialproject3.R
import com.example.trialproject3.Utility.LoadingSpinnerOverlay
import com.example.trialproject3.Utility.ToastHelper
import com.example.trialproject3.databinding.FragmentCartBinding
import java.io.Serializable

class CartFragment : Fragment(), OnBackPressedListener, CartAdapter.OnIncreaseCartClickListener,
    CartAdapter.OnDecreaseCartClickListener {
    private lateinit var binding: FragmentCartBinding
    private lateinit var context: Context
    private lateinit var builder: AlertDialog.Builder
    private lateinit var toastHelper: ToastHelper
    private lateinit var cartAdapter: CartAdapter
    private lateinit var loadingSpinnerOverlay: LoadingSpinnerOverlay

    companion object {
        private const val TAG = "CartFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        context = requireContext()
        builder = AlertDialog.Builder(context)
        toastHelper = ToastHelper(context)
        loadingSpinnerOverlay = LoadingSpinnerOverlay(context)

        binding.emptyTxt.visibility = View.GONE

        binding.orderNowBtn.setOnClickListener {
            builder.setTitle("Are you sure?")
                .setMessage("Do you want to purchase these items?")
                .setCancelable(true)
                .setPositiveButton("Yes") { dialog: DialogInterface?, which: Int ->
                }
                .setNegativeButton("No") { dialog: DialogInterface, which: Int -> dialog.cancel() }
                .show()
        }

        binding.backBtn.setOnClickListener { backToHomeFragment() }

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
                        CartAdapter(context, cartsList, this@CartFragment, this@CartFragment)
                    binding.cartRecyclerView.layoutManager = LinearLayoutManager(context)
                    binding.cartRecyclerView.adapter = cartAdapter
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
        toastHelper.showToast(cartModel.quantity.toString(), 0)
        updateQuantity(cartModel)
    }

    override fun onDecreaseCartClick(cartModel: CartModel) {
        if (cartModel.quantity > 0) {
            cartModel.quantity--
        }
        toastHelper.showToast(cartModel.quantity.toString(), 0)
        updateQuantity(cartModel)
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
//    @SuppressLint("SetTextI18n")
//    private fun calculateCart() {
//        val percentTax = 0.02
//        val delivery = 10.0
//        val tax = Math.round(managementCart.getTotalFee() * percentTax * 100.0) / 100.0
//        val total = Math.round((managementCart.getTotalFee() + tax + delivery) * 100.0) / 100.0
//        val itemTotal = (Math.round(managementCart.getTotalFee() * 100) / 100).toDouble()
//        binding.totalFeeTxt.text = "₱$itemTotal"
//        binding.taxTxt.text = "₱$tax"
//        binding.deliveryTxt.text = "₱$delivery"
//        binding.totalTxt.text = "₱$total"
//    }
}