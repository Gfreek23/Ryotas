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
    private val onDecreaseCartClickListener: OnDecreaseCartClickListener,
    private val onRemoveCartItemClickListener: OnRemoveCartItemClickListener
) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    interface OnIncreaseCartClickListener {
        fun onIncreaseCartClick(cartModel: CartModel)
    }

    interface OnDecreaseCartClickListener {
        fun onDecreaseCartClick(cartModel: CartModel)
    }

    interface OnRemoveCartItemClickListener {
        fun onRemoveCartItemClick(cartModel: CartModel)
    }

    fun calculateTotalPrice(): Double {
        var totalPrice = 0.0
        for (cartModel in cartModelList) {
            totalPrice += cartModel.price * cartModel.quantity
        }
        return totalPrice
    }

    class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(
            context: Context, cartModel: CartModel,
            onIncreaseCartClickListener: OnIncreaseCartClickListener,
            onDecreaseCartClickListener: OnDecreaseCartClickListener,
            onRemoveCartItemClickListener: OnRemoveCartItemClickListener
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

            binding.removeItemBtn.setOnClickListener {
                onRemoveCartItemClickListener.onRemoveCartItemClick(cartModel)
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
            onDecreaseCartClickListener,
            onRemoveCartItemClickListener
        )
    }

    override fun getItemCount(): Int {
        return cartModelList.size
    }
}