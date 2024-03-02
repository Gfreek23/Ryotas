package com.example.trialproject3.Fragment;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trialproject3.Activity.MainActivity;
import com.example.trialproject3.Activity.ProfileActivity;
import com.example.trialproject3.Activity.ToCancelActivity;
import com.example.trialproject3.Activity.ToReceiveActivity;
import com.example.trialproject3.Activity.ToShipActivity;
import com.example.trialproject3.Domain.PopularDomain;
import com.example.trialproject3.Firebase.FirebaseHelper;
import com.example.trialproject3.Helper.AlertDialogHelper;
import com.example.trialproject3.R;
import com.example.trialproject3.databinding.FragmentProfileBinding;
import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.List;

public class ProfileFragment extends Fragment implements MainActivity.OnBackPressedListener{
    private final String TAG = "ProfileFragment";
    private Context context;
    private FragmentProfileBinding binding;
    private Intent intent;
    private AlertDialogHelper alertDialogHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        context = getContext();
        alertDialogHelper = new AlertDialogHelper(context);

        if (FirebaseHelper.getUser() != null) {
            final String userID = FirebaseHelper.getUser().getUid();

            DocumentReference documentReference = FirebaseHelper.getFireStoreInstance()
                    .collection("users").document(userID);

            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String getFirstName = documentSnapshot.getString("Fname");
                    String getEmail = documentSnapshot.getString("email");

                    binding.fullNameTextView.setText(getFirstName);
                    binding.emailTextView.setText(getEmail);
                }
            });
        }

        binding.toShipBtn.setOnClickListener(v -> {
            // Retrieve the cart items from the intent
//            List<PopularDomain> cartItems = (List<PopularDomain>) getIntent().getSerializableExtra("cartItems");

            // Start ToShipActivity and pass the cart items
            Intent intent = new Intent(context, ToShipActivity.class);
//            intent.putExtra("cartItems", (Serializable) cartItems);
            startActivity(intent);
            requireActivity().finish();
        });
        binding.toPayBtn.setOnClickListener(v -> {
            intent = new Intent(context, ToShipActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        binding.toReceiveBtn.setOnClickListener(v -> {
            intent = new Intent(context, ToReceiveActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        binding.cancelBtn.setOnClickListener(v -> {
            intent = new Intent(context, ToCancelActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        binding.backBtn.setOnClickListener(v -> startActivity(new Intent(context, MainActivity.class)));

        binding.logoutBtn.setOnClickListener(v -> {
            alertDialogHelper.showAlertDialog("Logout", "Are you sure you want to Logout?",
                    "Logout", (dialog, which) -> FirebaseHelper.signOutUser(),
                    "Cancel", (dialog, which) -> alertDialogHelper.dismissDialog());
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isAdded()) {
            loadUserProfile();
        }
    }

    @Override
    public boolean onBackPressed() {
        backToHomeFragment();
        return true;
    }
    private void loadUserProfile() {
        if (FirebaseHelper.getUser() != null) {
            String userID = FirebaseHelper.getUser().getUid();

            DocumentReference documentReference = FirebaseHelper.getFireStoreInstance()
                    .collection("users").document(userID);

            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String getFirstName = documentSnapshot.getString("Fname");
                    String getEmail = documentSnapshot.getString("email");

                    binding.fullNameTextView.setText(getFirstName);
                    binding.emailTextView.setText(getEmail);
                }
            });
        }
    }



    private void backToHomeFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, new HomeFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


}