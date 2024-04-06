package com.example.trialproject3.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.trialproject3.Fragment.CartFragment;
import com.example.trialproject3.Fragment.HomeFragment;
import com.example.trialproject3.Fragment.PostsFragment;
import com.example.trialproject3.Fragment.ProfileFragment;
import com.example.trialproject3.Helper.AlertDialogHelper;
import com.example.trialproject3.Map.MapboxMapActivity;
import com.example.trialproject3.R;
import com.example.trialproject3.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    public static String fullName;
    public static String userType;
    public interface OnBackPressedListener {
        boolean onBackPressed();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        showFragment(new HomeFragment());
        initializeBottomNavBar();

        binding.extendedFabBtn.setOnClickListener(v -> showFragment(new PostsFragment()));

//        House.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                intent = new Intent(MainActivity.this, MapActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        HnB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                intent = new Intent(MainActivity.this, MapActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });

    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        if (currentFragment instanceof OnBackPressedListener) {
            boolean handled = ((OnBackPressedListener) currentFragment).onBackPressed();

            if (handled) {
                return;
            }
        }
        super.onBackPressed();
    }


    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    private void initializeBottomNavBar() {
        binding.bottomNavBar.setSelectedItemId(R.id.navHomeServices);
        binding.bottomNavBar.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.navHomeServices) {
                showFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.navExplore) {
                Intent intent = new Intent(MainActivity.this, MapboxMapActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.navCart) {
                showFragment(new CartFragment());
            } else if (item.getItemId() == R.id.navProfile) {
                showFragment(new ProfileFragment());
            }

            return true;
        });

    }
}