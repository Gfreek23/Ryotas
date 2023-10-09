package com.example.trialproject3.Activity;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.trialproject3.Adapter.CartListAdapter;
import com.example.trialproject3.Adapter.CategoryAdapter;
import com.example.trialproject3.Adapter.PopularListAdapter;
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

public class MainActivity extends AppCompatActivity {
private RecyclerView.Adapter adapterPupolar;

TextView Fname;
FirebaseAuth fauth;
FirebaseFirestore fstore;
String userId;

private RecyclerView recyclerViewPupolar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fname = findViewById(R.id.Fname);

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        userId = fauth.getCurrentUser().getUid();

        DocumentReference documentReference = fstore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Fname.setText(value.getString("Fname"));
            }
        });



        initRecyclerview();
        bottom_navigation();
        
    }



    private void bottom_navigation() {
        LinearLayout homeBtn=findViewById(R.id.homeBtn);
        LinearLayout cartBtn=findViewById(R.id.cartBtn);
        LinearLayout profileBtn=findViewById(R.id.profileBtn);

        homeBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MainActivity.class)));
        cartBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));
        //profileBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileAtivity.class)));
    }



    private void initRecyclerview() {

        ArrayList<PopularDomain> items=new ArrayList<>();
        items.add(new PopularDomain("Vegetables","Welcome to the vibrant and nourishing world of our vegetable\n" +
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
                "                 towards a more vibrant and nourished you!","pic1", 156,4.5,  100));
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
                "                 towards a more vibrant and nourished you!", "pic3", 187,4.9, 1000));

        recyclerViewPupolar=findViewById(R.id.view1);
        recyclerViewPupolar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        adapterPupolar=new PopularListAdapter(items);
        recyclerViewPupolar.setAdapter(adapterPupolar);
    }
}