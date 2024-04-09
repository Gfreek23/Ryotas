package com.example.trialproject3.Stores;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.trialproject3.Activity.CartActivity;
import com.example.trialproject3.Activity.MainActivity;
import com.example.trialproject3.Activity.ProfileActivity;
import com.example.trialproject3.Adapter.CartListAdapter;
import com.example.trialproject3.Adapter.CategoryAdapter;
import com.example.trialproject3.Adapter.PopularListAdapter;
import com.example.trialproject3.CartActivityBackBtn.CartActivityBackBtn1;
import com.example.trialproject3.Domain.CategoryDomain;
import com.example.trialproject3.Domain.PopularDomain;
import com.example.trialproject3.Models.CategoryModels;
import com.example.trialproject3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class Vegetables_Store1 extends AppCompatActivity {

    private RecyclerView.Adapter adapterPupolar;

    FirebaseAuth fauth;

    String userId;

    FirebaseFirestore fstore;

    private RecyclerView recyclerViewPupolar, recyclerViewPupolar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vegetables_store1);

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        userId = fauth.getCurrentUser().getUid();

        initRecyclerview();
        bottom_navigation();


    }


    private void bottom_navigation() {
        LinearLayout homeBtn = findViewById(R.id.homeBtn);
        LinearLayout cartBtn = findViewById(R.id.cartBtn);
        LinearLayout profileBtn = findViewById(R.id.profileBtn);

        homeBtn.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        cartBtn.setOnClickListener(v -> startActivity(new Intent(this, CartActivityBackBtn1.class)));

        // Update the profileBtn click listener
        profileBtn.setOnClickListener(v -> {
            Intent profileIntent = new Intent(this, ProfileActivity.class);
            startActivity(profileIntent);
        });
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
        items.add(new PopularDomain("Tomato", "Freshly picked tomatoes", "tomato", 156, 4.5, 40));
        items.add(new PopularDomain("Meat", "Fresh chopped meat", "karne", 132, 4.8, 300));
        items.add(new PopularDomain("Perfume", "Indulge in the allure of our exquisite fragrance, where sophistication meets sensuality.", "perfume", 187, 4.9, 800));

        recyclerViewPupolar = findViewById(R.id.view1);
        recyclerViewPupolar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewPupolar2 = findViewById(R.id.view10);
        recyclerViewPupolar2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        adapterPupolar = new PopularListAdapter(items);
        recyclerViewPupolar.setAdapter(adapterPupolar);
        adapterPupolar = new PopularListAdapter(items);
        recyclerViewPupolar2.setAdapter(adapterPupolar);
    }
}