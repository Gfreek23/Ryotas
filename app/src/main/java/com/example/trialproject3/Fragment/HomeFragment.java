package com.example.trialproject3.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.example.trialproject3.Activity.LoginActivity;
import com.example.trialproject3.Activity.MainActivity;
import com.example.trialproject3.Map.MapActivity;
import com.example.trialproject3.Adapter.PopularListAdapter;
import com.example.trialproject3.Domain.PopularDomain;
import com.example.trialproject3.Firebase.FirebaseHelper;
import com.example.trialproject3.Helper.AlertDialogHelper;
import com.example.trialproject3.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements MainActivity.OnBackPressedListener{
    private final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private Context context;
    private Intent intent;
    private RecyclerView.Adapter popularRecyclerViewAdapter;
    private AlertDialogHelper alertDialogHelper;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        context = getContext();
        alertDialogHelper = new AlertDialogHelper(context);

        if (FirebaseHelper.getUser() == null) {
            intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }

        binding.searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        binding.searchEditText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                performSearch();
                return true;
            }
            return false;
        });


        binding.fruitsAndVegBtn.setOnClickListener(v -> {
            intent = new Intent(context, MapActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        binding.dairyProductsBtn.setOnClickListener(v -> {
            intent = new Intent(context, MapActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        binding.meatProductsBtn.setOnClickListener(v -> {
            intent = new Intent(context, MapActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        displayUsername();
        initRecyclerview();
    }

    @Override
    public boolean onBackPressed() {
        exitApp();
        return true;
    }

    private void exitApp() {
        alertDialogHelper.showAlertDialog("Exit App", "Are you sure you want to Exit App?",
                "Exit", (dialog, which) -> requireActivity().finish(),
                "Cancel", (dialog, which) -> alertDialogHelper.dismissDialog());
    }

    private void displayUsername() {
        if (FirebaseHelper.getUser() != null) {
            DocumentReference documentReference = FirebaseHelper.getFireStoreInstance()
                    .collection("users").document(FirebaseHelper.getUser().getUid());

            documentReference.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String getFirstName = documentSnapshot.getString("Fname");
                        binding.firstNameTextView.setText(getFirstName);
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "displayUsername: " + TAG + e.getMessage()));
        }
    }

    private void performSearch() {
        String searchQuery = binding.searchEditText.getText().toString().trim();
        if (!searchQuery.isEmpty()) {
            Intent intent = new Intent(context, MapActivity.class);
            intent.putExtra("searchQuery", searchQuery);
            startActivity(intent);
        }
    }

    private void initRecyclerview() {

        ArrayList<PopularDomain> items = new ArrayList<>();
        items.add(new PopularDomain("Vegetables", "Welcome to the vibrant and nourishing world of our vegetable\n" +
                "                 market! Step into a realm where colors, freshness, and flavors\n" +
                "                 intertwine to awaken your senses and inspire your culinary\n" +
                "                 adventures. Discover a bountiful array of seasonal treasures,\n" +
                "                 as we showcase an abundant selection of wholesome and\n" +
                "                 locally sourced vegetables, straight from the fields to your\n" +
                "                 table. From the crisp green vitality of leafy greens to the vibrant\n" +
                "                 hues of tantalizing root vegetables, we invite you to explore\n" +
                "                 the natural goodness that Mother Nature has to offer. Whether\n" +
                "                 you're a seasoned cook or an aspiring food-lover, our vegetable\n" +
                "                 market is a haven where you can cultivate a healthier lifestyle\n" +
                "                 and celebrate the veritable symphony of tastes and textures\n" +
                "                 that only vegetables can provide. So, come and immerse\n" +
                "                 yourself in the kaleidoscope of flavors, relish the farm-to-fork\n" +
                "                 experience, and let our vegetable market be your guiding light\n" +
                "                 towards a more vibrant and nourished you!", "pic1", 156, 4.5, 100));
        items.add(new PopularDomain("Meat", "Welcome to the vibrant and nourishing world of our vegetable\n" +
                "                 market! Step into a realm where colors, freshness, and flavors\n" +
                "                 intertwine to awaken your senses and inspire your culinary\n" +
                "                 adventures. Discover a bountiful array of seasonal treasures,\n" +
                "                 as we showcase an abundant selection of wholesome and\n" +
                "                 locally sourced vegetables, straight from the fields to your\n" +
                "                 table. From the crisp green vitality of leafy greens to the vibrant\n" +
                "                 hues of tantalizing root vegetables, we invite you to explore\n" +
                "                 the natural goodness that Mother Nature has to offer. Whether\n" +
                "                 you're a seasoned cook or an aspiring food-lover, our vegetable\n" +
                "                 market is a haven where you can cultivate a healthier lifestyle\n" +
                "                 and celebrate the veritable symphony of tastes and textures\n" +
                "                 that only vegetables can provide. So, come and immerse\n" +
                "                 yourself in the kaleidoscope of flavors, relish the farm-to-fork\n" +
                "                 experience, and let our vegetable market be your guiding light\n" +
                "                 towards a more vibrant and nourished you!", "pic2", 132, 4.8, 300));
        items.add(new PopularDomain("HouseHold", "Welcome to the vibrant and nourishing world of our vegetable\n" +
                "                 market! Step into a realm where colors, freshness, and flavors\n" +
                "                 intertwine to awaken your senses and inspire your culinary\n" +
                "                 adventures. Discover a bountiful array of seasonal treasures,\n" +
                "                  as we showcase an abundant selection of wholesome and\n" +
                "                 locally sourced vegetables, straight from the fields to your\n" +
                "                 table. From the crisp green vitality of leafy greens to the vibrant\n" +
                "                 hues of tantalizing root vegetables, we invite you to explore\n" +
                "                 the natural goodness that Mother Nature has to offer. Whether\n" +
                "                 you're a seasoned cook or an aspiring food-lover, our vegetable\n" +
                "                 market is a haven where you can cultivate a healthier lifestyle\n" +
                "                 and celebrate the veritable symphony of tastes and textures\n" +
                "                 that only vegetables can provide. So, come and immerse\n" +
                "                 yourself in the kaleidoscope of flavors, relish the farm-to-fork\n" +
                "                 experience, and let our vegetable market be your guiding light\n" +
                "                 towards a more vibrant and nourished you!", "pic3", 187, 4.9, 1000));


        binding.popularRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        popularRecyclerViewAdapter = new PopularListAdapter(items);
        binding.popularRecyclerView.setAdapter(popularRecyclerViewAdapter);
    }


}