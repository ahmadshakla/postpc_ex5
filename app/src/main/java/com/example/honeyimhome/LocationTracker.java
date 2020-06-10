package com.example.honeyimhome;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationTracker {
    private static final int FAST_UPDATE = 5000;
    private static final int NORMAL_UPDATE = 30000;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private static final String ERR_MSG = "you don't have location permission";
    private static final String TAG = "Location Tracker";

    private Context context;
    private TextView longitude;
    private TextView latitude;
    private TextView accuracy;

    public LocationTracker(Context context, TextView latitude, TextView longitude, TextView accuracy) {
        this.context = context;
        this.accuracy = accuracy;
        this.longitude = longitude;
        this.latitude = latitude;
        locationRequest = new LocationRequest();
        locationRequest.setInterval(NORMAL_UPDATE);
        locationRequest.setFastestInterval(FAST_UPDATE);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    public void startTracking() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_DENIED) {

            Log.e(TAG, ERR_MSG);
            return;
        }
        else {

            updateUI();
        }



    }

    private void updateUI() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener( new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
            latitude.setText(String.valueOf(location.getLatitude()));
            longitude.setText(String.valueOf(location.getLongitude()));
            accuracy.setText(String.valueOf(location.getAccuracy()));
            }
        });
    }

    public void stopTracking() {

    }
}
