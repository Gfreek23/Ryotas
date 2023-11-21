package com.example.trialproject3.Activity;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class FetchData extends AsyncTask<String, Void, String> {
    private GoogleMap googleMap;

    public FetchData(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    protected String doInBackground(String... strings) {
        String googleNearByPlacesData = "";

        try {
            String url = strings[0];
            DownloadUrl downloadUrl = new DownloadUrl();
            googleNearByPlacesData = downloadUrl.retireveUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleNearByPlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
        if (s != null && !s.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject placeObject = jsonArray.getJSONObject(i);
                    JSONObject locationObject = placeObject.getJSONObject("geometry")
                            .getJSONObject("location");

                    double lat = locationObject.getDouble("lat");
                    double lng = locationObject.getDouble("lng");
                    String name = placeObject.getString("name");

                    LatLng latLng = new LatLng(lat, lng);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.title(name);
                    markerOptions.position(latLng);
                    googleMap.addMarker(markerOptions);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // Handle empty or null data here
        }
    }
}
