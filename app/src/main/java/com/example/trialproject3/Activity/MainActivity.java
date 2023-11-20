package com.example.trialproject3.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trialproject3.Adapter.PopularListAdapter;
import com.example.trialproject3.Domain.PopularDomain;
import com.example.trialproject3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
private RecyclerView.Adapter adapterPupolar;
TextView Fname,Fruits,Dairy,Karne,House,HnB;

ImageView Fname1,Fruits1,Dairy1,Karne1,House1,HnB1;
FirebaseAuth fauth;
FirebaseFirestore fstore;
String userId;
private EditText searchEditText;

private RecyclerView recyclerViewPupolar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fname = findViewById(R.id.Fname);

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        userId = fauth.getCurrentUser().getUid();

        Fruits = findViewById(R.id.FnV);
        Dairy = findViewById(R.id.DairP);
        Karne = findViewById(R.id.KarneP);
        House = findViewById(R.id.HouseP);
        HnB = findViewById(R.id.HnBP);

        searchEditText = findViewById(R.id.editTextText);

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        searchEditText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                performSearch();
                return true;
            }
            return false;
        });


        Fruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Dairy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Karne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
            }
        });

        House.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
            }
        });

        HnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
            }
        });

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

    private void performSearch() {
        String searchQuery = searchEditText.getText().toString().trim();
        if (!searchQuery.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            intent.putExtra("searchQuery", searchQuery);
            startActivity(intent);
        }
    }



    private void bottom_navigation() {
        LinearLayout homeBtn=findViewById(R.id.homeBtn);
        LinearLayout cartBtn=findViewById(R.id.cartBtn);
        LinearLayout profileBtn=findViewById(R.id.profileBtn);

        homeBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MainActivity.class)));
        cartBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));
        profileBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
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

        recyclerViewPupolar=findViewById(R.id.view9);
        recyclerViewPupolar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        adapterPupolar=new PopularListAdapter(items);
        recyclerViewPupolar.setAdapter(adapterPupolar);
    }
}