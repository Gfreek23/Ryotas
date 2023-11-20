package com.example.trialproject3.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.trialproject3.R;
import com.example.trialproject3.databinding.ActivityMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Strings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MapSearch extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityMapBinding binding2;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int  Request_code = 101;
    private double lat, lng;
    ImageButton atm, store, hosp, res;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);

        atm=findViewById(R.id.BANK);
        store=findViewById(R.id.store);
        hosp=findViewById(R.id.hospital);
        res=findViewById(R.id.Res);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getApplicationContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);


        atm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder stringBuilder= new StringBuilder("http://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                stringBuilder.append("location" + lat + "," + lng);
                stringBuilder.append("&radius=1000");
                stringBuilder.append("&type=atm");
                stringBuilder.append("&sensor=true");
                stringBuilder.append("&key=" + getResources().getString(R.string.google_map_key));

                String url = stringBuilder.toString();
                Object dataFetch[]=new Object[2];
                dataFetch[0]=mMap;
                dataFetch[1]=url;

                FetchData fetchData=new FetchData();
                fetchData.execute(dataFetch);
            }
        });
        hosp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder stringBuilder= new StringBuilder("http://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                stringBuilder.append("location" + lat + "," + lng);
                stringBuilder.append("&radius=1000");
                stringBuilder.append("&type=hospital");
                stringBuilder.append("&sensor=true");
                stringBuilder.append("&key=" + getResources().getString(R.string.google_map_key));

                String url = stringBuilder.toString();
                Object dataFetch[]=new Object[2];
                dataFetch[0]=mMap;
                dataFetch[1]=url;

                FetchData fetchData=new FetchData();
                fetchData.execute(dataFetch);
            }
        });

        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder stringBuilder= new StringBuilder("http://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                stringBuilder.append("location" + lat + "," + lng);
                stringBuilder.append("&radius=1000");
                stringBuilder.append("&type=store");
                stringBuilder.append("&sensor=true");
                stringBuilder.append("&key=" + getResources().getString(R.string.google_map_key));

                String url = stringBuilder.toString();
                Object dataFetch[]=new Object[2];
                dataFetch[0]=mMap;
                dataFetch[1]=url;

                FetchData fetchData=new FetchData();
                fetchData.execute(dataFetch);

            }
        });

        res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder stringBuilder= new StringBuilder("http://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                stringBuilder.append("location" + lat + "," + lng);
                stringBuilder.append("&radius=1000");
                stringBuilder.append("&type=restaurant");
                stringBuilder.append("&sensor=true");
                stringBuilder.append("&key=" + getResources().getString(R.string.google_map_key));

                String url = stringBuilder.toString();
                Object dataFetch[]=new Object[2];
                dataFetch[0]=mMap;
                dataFetch[1]=url;

                FetchData fetchData=new FetchData();
                fetchData.execute(dataFetch);
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        getCurrentLocation();
    }

    private void getCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_code);
         return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(60000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(5000);
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Toast.makeText(getApplicationContext(),"location result is=" + locationResult
                        ,Toast.LENGTH_SHORT).show();

                if (locationResult== null){
                    Toast.makeText(getApplicationContext(),"Current location is null",Toast.LENGTH_LONG).show();

                    return;
                }
                for (Location location:locationResult.getLocations()){
                    if(location!=null){
                        Toast.makeText(getApplicationContext(),"Current location is=" + location.getLongitude()
                                ,Toast.LENGTH_LONG).show();
                    }
                }
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null);

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location!=null){
                    lat = location.getLatitude();
                    lng = location.getLongitude();

                    LatLng latLng = new LatLng(lat,lng);
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Current location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                }
            }
        });
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (Request_code){

            case Request_code:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getCurrentLocation();
                }
        }
    }
}



