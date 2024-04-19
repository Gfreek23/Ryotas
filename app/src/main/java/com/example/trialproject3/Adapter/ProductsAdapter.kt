package com.example.trialproject3.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trialproject3.Activity.MainActivity
import com.example.trialproject3.Models.ProductsModel
import com.example.trialproject3.databinding.ItemProductsBinding

class ProductsAdapter(
    private val context: Context,
    private val productsModelList: List<ProductsModel>,
    private val onProductItemClickListener: OnProductItemClickListener
) :
    RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    interface OnProductItemClickListener {
        fun onProductItemClick(productsModel: ProductsModel)
    }

    class ProductViewHolder(val binding: ItemProductsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(
            context: Context,
            productsModel: ProductsModel,
            onProductItemClickListener: OnProductItemClickListener
        ) {
            binding.addToCartImageView.visibility = View.GONE

            binding.sellerNameTextView.text = productsModel.sellerName
            binding.emailTextView.text = productsModel.sellerEmail
            binding.productNameTextView.text = productsModel.productName
            binding.priceTextView.text = "₱ ${productsModel.price}"
            binding.productDescriptionTextView.text = productsModel.productDescription

            if (MainActivity.userType == "Buyer") {
                binding.addToCartImageView.visibility = View.VISIBLE
            }

            if (productsModel.productImage != "none") {
                Glide.with(context)
                    .load(productsModel.productImage)
                    .into(binding.productImageView)
            }

            binding.addToCartImageView.setOnClickListener {
                onProductItemClickListener.onProductItemClick(productsModel)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val binding =
            ItemProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(context, productsModelList[position], onProductItemClickListener)
    }

    override fun getItemCount(): Int {
        return productsModelList.size
    }
}