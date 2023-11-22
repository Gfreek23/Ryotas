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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trialproject3.R;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class MapSearch extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int Request_code = 101;
    private double lat, lng;
    ImageButton atm, store, hosp, res;
    private HashMap<String, Marker> markerMap = new HashMap<>();

    private void clearMarkers() {
        mMap.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);

        atm = findViewById(R.id.BANK);
        store = findViewById(R.id.store);
        hosp = findViewById(R.id.hospital);
        res = findViewById(R.id.Res);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getApplicationContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);


        atm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMarkers();
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                        "location=" + lat + "," + lng +
                        "&radius=1000" +
                        "&type=pharmacy" +
                        "&key=" + getResources().getString(R.string.google_map_key);

                new FetchData(mMap).execute(url);
            }
        });

        hosp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMarkers();
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                        "location=" + lat + "," + lng +
                        "&radius=1000" +
                        "&type=wet_market" +
                        "&key=" + getResources().getString(R.string.google_map_key);

                new FetchData(mMap).execute(url);
            }
        });

        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMarkers();
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                        "location=" + lat + "," + lng +
                        "&radius=1000" +
                        "&type=convenience store" +
                        "&key=" + getResources().getString(R.string.google_map_key);

                new FetchData(mMap).execute(url);
            }
        });

        res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMarkers();
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                        "location=" + lat + "," + lng +
                        "&radius=1000" +
                        "&type=hardware_store" +
                        "&key=" + getResources().getString(R.string.google_map_key);

                new FetchData(mMap).execute(url);
            }
        });
    }

        public class MarkerInfo {
            private String name;
            private String info;
            private String email;
            private String phone;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getInfo() {
                return info;
            }

            public void setInfo(String info) {
                this.info = info;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }
        }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String placeId = (String) marker.getTag();

                if (placeId != null) {
                    String placeDetailsUrl = "https://maps.googleapis.com/maps/api/place/details/json?" +
                            "place_id=" + placeId +
                            "&fields=name,formatted_phone_number,website" +
                            "&key=" + getResources().getString(R.string.google_map_key);

                    new FetchPlaceDetailsTask().execute(placeDetailsUrl);
                }

                return false;
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View infoView = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                ImageView imageView = infoView.findViewById(R.id.info_image);
                TextView titleTextView = infoView.findViewById(R.id.info_title);
                TextView snippetTextView = infoView.findViewById(R.id.info_snippet);
                TextView emailTextView = infoView.findViewById(R.id.info_email);
                TextView phoneTextView = infoView.findViewById(R.id.info_phone);


                if (marker.getTag() != null) {

                    String name = "Store Name";
                    String info = "Store info";
                    String email = "store@example.com";
                    String phone = "+1234567890";

                    titleTextView.setText(name);
                    snippetTextView.setText(info);
                    emailTextView.setText(email);
                    phoneTextView.setText(phone);
                    // Set image using Picasso/Glide or other image loading libraries
                    // Example: Picasso.get().load(imageUrl).into(imageView);
                }

                return infoView;
            }
        });

        getCurrentLocation();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_code);
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(60000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(5000);
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Toast.makeText(getApplicationContext(), "location result is=" + locationResult
                        , Toast.LENGTH_SHORT).show();

                if (locationResult == null) {
                    Toast.makeText(getApplicationContext(), "Current location is null", Toast.LENGTH_LONG).show();

                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        Toast.makeText(getApplicationContext(), "Current location is=" + location.getLongitude()
                                , Toast.LENGTH_LONG).show();
                    }
                }
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();

                    LatLng latLng = new LatLng(lat, lng);
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Current location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            }
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Request_code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                }
                break;
        }
    }



    private class FetchPlaceDetailsTask extends AsyncTask<String, Void, MarkerInfo> {
        @Override
        protected MarkerInfo doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            String placeDetailsUrl = urls[0];
            MarkerInfo markerInfo = null;

            try {
                URL url = new URL(placeDetailsUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                // Parse JSON response here and populate markerInfo
                JSONObject jsonResponse = new JSONObject(builder.toString());

                // Example of parsing the JSON to fetch information
                String name = jsonResponse.optString("name", "Store Name");
                String info = jsonResponse.optString("info", "Store Info");
                String email = jsonResponse.optString("email", "store@example.com");
                String phone = jsonResponse.optString("phone", "+1234567890");

                markerInfo = new MarkerInfo();
                markerInfo.setName(name);
                markerInfo.setInfo(info);
                markerInfo.setEmail(email);
                markerInfo.setPhone(phone);

                inputStream.close();
                urlConnection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return markerInfo;
        }

        @Override
        protected void onPostExecute(MarkerInfo markerInfo) {
            if (markerInfo != null) {
                String name = markerInfo.getName();
                String info = markerInfo.getInfo();
                String email = markerInfo.getEmail();
                String phone = markerInfo.getPhone();
                if (mMap != null) {
                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                            View infoView = getLayoutInflater().inflate(R.layout.custom_info_window, null);


                            TextView titleTextView = infoView.findViewById(R.id.info_title);
                            TextView snippetTextView = infoView.findViewById(R.id.info_snippet);
                            TextView emailTextView = infoView.findViewById(R.id.info_email);
                            TextView phoneTextView = infoView.findViewById(R.id.info_phone);

                            titleTextView.setText(name);
                            snippetTextView.setText(info);
                            emailTextView.setText(email);
                            phoneTextView.setText(phone);

                            return infoView;
                        }
                    });

                    String markerTag = "";
                    Marker clickedMarker = markerMap.get(markerTag);
                    if (clickedMarker != null) {
                        clickedMarker.showInfoWindow();
                    }
                }
            } else {
                // Handle case where markerInfo is null or fetching failed
            }
        }
    }
}