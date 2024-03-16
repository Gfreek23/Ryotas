package com.example.trialproject3.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.SearchView;


import com.example.trialproject3.Stores.Vegetables_Store1;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.example.trialproject3.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    double searchLatitude;
    double searchLongitude;
    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap mMap;
    private SearchView mapSearchView;
    private Marker marker;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapSearchView = findViewById(R.id.MapSearch);

        Intent intent = getIntent();
        searchLatitude = intent.getDoubleExtra("latitude", 0);
        searchLongitude = intent.getDoubleExtra("longitude", 0);
        String searchQuery = intent.getStringExtra("searchQuery");


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                String location = mapSearchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null){
                    Geocoder geocoder = new Geocoder(MapActivity.this);

                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



    }



    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    currentLocation = location;

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(MapActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;



        LatLng davao = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(davao));
        MarkerOptions options = new MarkerOptions().position(davao).title("My Location");
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.addMarker(options);


        LatLng tindahan4 = new LatLng(7.120952781542856, 125.64528472219986);
        mMap.addMarker(new MarkerOptions().position(tindahan4).title("Tindahan sa Gulay")
                .icon(bitmapDescriptor(getApplicationContext(),R.drawable.vegetables)));
        mMap.setOnMarkerClickListener(this);



        LatLng tindahan2 = new LatLng(7.121976721489662, 125.64905935449492);
        mMap.addMarker(new MarkerOptions().position(tindahan2).title("Tindahan sa Gulay")
                .icon(bitmapDescriptor(getApplicationContext(),R.drawable.vegetables)));
        mMap.setOnMarkerClickListener(this);

        LatLng tindahan3 = new LatLng(7.120976484911648, 125.64939183514572);
        mMap.addMarker(new MarkerOptions().position(tindahan3).title("Tindahan sa Gulay")
                .icon(bitmapDescriptor(getApplicationContext(),R.drawable.vegetables)));
        mMap.setOnMarkerClickListener(this);


        CameraUpdate center = CameraUpdateFactory.newLatLng(davao);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        mMap.moveCamera(center);
        mMap.moveCamera(zoom);

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        Intent intent = new Intent(this, Vegetables_Store1.class);
        startActivity(intent);

        return false;
    }

    private BitmapDescriptor bitmapDescriptor(Context applicationContext, int vegetables) {
        Drawable vectorDrawable = ContextCompat.getDrawable(applicationContext, vegetables);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else {
                Toast.makeText(this, "Location permission is denied, please allow the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }



}