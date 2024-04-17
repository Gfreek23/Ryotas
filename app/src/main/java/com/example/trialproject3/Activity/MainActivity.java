package com.example.trialproject3.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.trialproject3.Firebase.FirebaseHelper;
import com.example.trialproject3.Fragment.CartFragment;
import com.example.trialproject3.Fragment.HomeFragment;
import com.example.trialproject3.Fragment.PostContentFragment;
import com.example.trialproject3.Fragment.PostsFragment;
import com.example.trialproject3.Fragment.ProfileFragment;
import com.example.trialproject3.Map.MapboxMapActivity;
import com.example.trialproject3.R;
import com.example.trialproject3.databinding.ActivityMainBinding;
import com.google.firebase.firestore.DocumentSnapshot;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    public static String fullName;
    public static String userType;
    public static String profilePicture;
    private Intent intent;

    public interface OnBackPressedListener {
        boolean onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.extendedFabBtn.setVisibility(View.GONE);
        initializeBottomNavBar();
        getUserDetails();

        intent = getIntent();
        if (intent.hasExtra("cart")) {
            showFragment(new CartFragment());
        } else {
            showFragment(new HomeFragment());
        }
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

    private void getUserDetails() {
        if (FirebaseHelper.currentUser() != null) {
            FirebaseHelper.currentUserDetails().get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            String getFullName = documentSnapshot.getString("Fname");
                            String getUserType = documentSnapshot.getString("UserType");
                            String getProfilePicture = documentSnapshot.getString("ProfilePicture");
                            MainActivity.fullName = getFullName;
                            MainActivity.userType = getUserType;
                            MainActivity.profilePicture = getProfilePicture;

                            if (getUserType.equals("Seller")) {
                                Menu menu = binding.bottomNavBar.getMenu();
                                MenuItem menuItem1  = menu.getItem(1);
                                MenuItem menuItem2 = menu.getItem(3);
//                                menuItem1.setVisible(false);
                                menuItem2.setVisible(false);

                                binding.extendedFabBtn.setVisibility(View.VISIBLE);
                                binding.extendedFabBtn.setOnClickListener(v -> showFragment(new PostContentFragment()));
                            }
                        } else {
                            Log.e(TAG, "getUserDetails: " + task.getException());
                        }
                    });

        }
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
            }else if (item.getItemId() == R.id.navPosts) {
                showFragment(new PostsFragment());
            } else if (item.getItemId() == R.id.navCart) {
                showFragment(new CartFragment());
            } else if (item.getItemId() == R.id.navProfile) {
                showFragment(new ProfileFragment());
            }

            return true;
        });
    }
}