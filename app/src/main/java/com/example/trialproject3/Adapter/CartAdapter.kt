package com.example.trialproject3.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trialproject3.Models.CartModel
import com.example.trialproject3.databinding.ItemCartBinding

class CartAdapter(
    private val context: Context,
    private val cartModelList: List<CartModel>,
    private val onIncreaseCartClickListener: OnIncreaseCartClickListener,
    private val onDecreaseCartClickListener: OnDecreaseCartClickListener
) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    interface OnIncreaseCartClickListener {
        fun onIncreaseCartClick(cartModel: CartModel)
    }

    interface OnDecreaseCartClickListener {
        fun onDecreaseCartClick(cartModel: CartModel)
    }
    var totalPrice: Double = 0.0

    init {
        calculateTotalPrice()
    }

    private fun calculateTotalPrice() {
        totalPrice = 0.0
        for (cartModel in cartModelList) {
            totalPrice += cartModel.price * cartModel.quantity
        }
    }
    class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(
            context: Context, cartModel: CartModel,
            onIncreaseCartClickListener: OnIncreaseCartClickListener,
            onDecreaseCartClickListener: OnDecreaseCartClickListener
        ) {
            binding.storeNameTextView.text = cartModel.storeName
            binding.productNameTextView.text = cartModel.productName
            binding.descriptionTextView.text = cartModel.productDescription
            binding.priceTextView.text = "₱ ${cartModel.price}"
            binding.quantityTextView.text = cartModel.quantity.toString()
            Glide.with(context).load(cartModel.productImage).into(binding.productImageView)

            binding.increaseQuantityBtn.setOnClickListener {
                onIncreaseCartClickListener.onIncreaseCartClick(cartModel)
            }
            binding.decreaseQuantityBtn.setOnClickListener {
                onDecreaseCartClickListener.onDecreaseCartClick(cartModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(
            context,
            cartModelList[position],
            onIncreaseCartClickListener,
            onDecreaseCartClickListener
        )
    }

    override fun getItemCount(): Int {
        return cartModelList.size
    }
}