package com.example.trialproject3.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.trialproject3.Domain.PopularDomain;
import com.example.trialproject3.Helper.ManagementCart;
import com.example.trialproject3.R;
import com.example.trialproject3.databinding.ActivityDetailBinding;

public class DetailActivity extends AppCompatActivity {
   private static final String TAG = "DetailActivity";
   private ActivityDetailBinding binding;
    private PopularDomain object;
    private int numberOrder = 1;
    private ManagementCart managementCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managementCart = new ManagementCart(this);
        getBundle();

    }

    @SuppressLint("SetTextI18n")
    private void getBundle() {
        object = (PopularDomain) getIntent().getSerializableExtra("object");
        int drawableResourceId = this.getResources().getIdentifier(object.getPicUrl(), "drawable", this.getPackageName());

        Glide.with(this)
                .load(drawableResourceId)
                .into(binding.itemImageView);

        binding.titleTxt.setText(object.getTitle());
        binding.priceTxt.setText("₱" + object.getPrice());
        binding.descriptionTxt.setText(object.getDescription());
        binding.reviewsTextView.setText(object.getReview() + "");
        binding.scoreTxt.setText(object.getScore() + "");

        binding.addToCartBtn.setOnClickListener(v -> {
            object.setNumberinCart(numberOrder);
            managementCart.insertFood(object);
            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
            intent.putExtra("cart", "cart");
            startActivity(intent);
            finish();
        });
        binding.backBtn.setOnClickListener(v -> finish());
    }
}