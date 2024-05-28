package com.example.trialproject3.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trialproject3.Activity.MainActivity
import com.example.trialproject3.Activity.ProductDetailsActivity
import com.example.trialproject3.Models.ProductsModel
import com.example.trialproject3.R
import com.example.trialproject3.databinding.ItemProductsBinding

class ProductsAdapter(
    private val context: Context,
    private val productsModelList: List<ProductsModel>,
) :
    RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    class ProductViewHolder(val binding: ItemProductsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(
            context: Context,
            productsModel: ProductsModel,
        ) {
            binding.productNameTextView.text = productsModel.productName
            binding.priceTextView.text = "₱ ${productsModel.price}"
            binding.productRatings.rating = productsModel.productRatings.toFloat()

            val sharedPreferences = context.getSharedPreferences(
                "currentUserPrefs",
                Context.MODE_PRIVATE
            )

            val userType = sharedPreferences.getString("userType", null)

            if (userType == "Buyer") {
                itemView.setOnClickListener {
                    val intent = Intent(context, ProductDetailsActivity::class.java)
                    intent.putExtra("productDetails", productsModel)
                    context.startActivity(intent)
                }
            }

            if (productsModel.productImage != "none") {
                Glide.with(context)
                    .load(productsModel.productImage)
                    .placeholder(R.drawable.loading_gif)
                    .into(binding.productImageView)
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
        holder.bind(context, productsModelList[position])
    }

    override fun getItemCount(): Int {
        return productsModelList.size
    }
}