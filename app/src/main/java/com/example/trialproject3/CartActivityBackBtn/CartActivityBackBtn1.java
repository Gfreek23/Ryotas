package com.example.trialproject3.CartActivityBackBtn;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trialproject3.Activity.AddressActivity;
import com.example.trialproject3.Activity.MainActivity;
import com.example.trialproject3.Adapter.CartListAdapter;
import com.example.trialproject3.Helper.ChangeNumberItemsListener;
import com.example.trialproject3.Helper.ManagementCart;
import com.example.trialproject3.R;
import com.example.trialproject3.Stores.Vegetables_Store1;

public class CartActivityBackBtn1 extends AppCompatActivity {
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private ManagementCart managementCart;

    private TextView totalFeeTxt,taxTxt,deliveryTxt,totalTxt,emptyTxt;
    private double tax;
    private ScrollView scrollView;
    private ImageView backBtn;

    private ImageView addBtn;

    Button btnfinal;
    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        addBtn = findViewById(R.id.imageView13);
        backBtn = findViewById(R.id.backBtn);
        btnfinal = findViewById(R.id.Order);
        builder = new AlertDialog.Builder(this);

        btnfinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                builder.setTitle("Are you sure?")
                        .setMessage("Do you want to purchase this items?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle("Are you sure?")
                        .setMessage("Do you want to leave your cart?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                startActivity(new Intent(CartActivityBackBtn1.this, Vegetables_Store1.class));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivityBackBtn1.this, AddressActivity.class));
            }
        });


        managementCart=new ManagementCart(this);

        initView();
        initList();
        calculateCart();

    }

    private void initList() {
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new CartListAdapter(managementCart.getListCart(), this, new ChangeNumberItemsListener() {
            @Override
            public void change() {
                calculateCart();
            }
        });

        recyclerView.setAdapter(adapter);
        if (managementCart.getListCart().isEmpty()){
            emptyTxt.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }else{
            emptyTxt.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
    }

    private void calculateCart() {
        double percentTax=0.02;
        double delivery=10;
        tax=Math.round((managementCart.getTotalFee() * percentTax * 100.0)) / 100.0;

        double total=Math.round((managementCart.getTotalFee() + tax + delivery) * 100.0) / 100.0;
        double itemTotal=Math.round(managementCart.getTotalFee() * 100) / 100;

        totalFeeTxt.setText("₱"+itemTotal);
        taxTxt.setText("₱"+tax);
        deliveryTxt.setText("₱"+delivery);
        totalTxt.setText("₱"+total);
    }

    private void initView() {
        totalFeeTxt=findViewById(R.id.totalFeeTxt);
        taxTxt=findViewById(R.id.taxTxt);
        deliveryTxt=findViewById(R.id.deliveryTxt);
        totalTxt=findViewById(R.id.totalTxt);
        recyclerView=findViewById(R.id.view3);
        scrollView=findViewById(R.id.scrollView3);
        emptyTxt=findViewById(R.id.emptyTxt);

    }


}