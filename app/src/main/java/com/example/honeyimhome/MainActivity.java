package com.example.honeyimhome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE = 99;
    private static final int FAST_UPDATE = 300;
    private static final int NORMAL_UPDATE = 1000;
    public static final int REFRESH_TIME = 5000;
    private static final String STATE = "tracking";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String SHOW_HOME_LOCATION = "show home location";
    public static final int MIN_ACCURAY = 50;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private TextView latitude;
    private TextView longitude;
    private TextView accuracy;
    private LocationManager locationManager;
    private LocationListener listener;

    private boolean tracking = false;
    private static final String SHARED_PREF = "shared preferences";
    SharedPreferences sharedPreferences;
    private Button showHomeLocation;
    private Button clearHomeLocation;
    private Button refresh;
    private TextView homeLocation;
    private Location currLocation;
    //    private LocationTracker locationTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latitude = findViewById(R.id.textView3);
        longitude = findViewById(R.id.textView7);
        accuracy = findViewById(R.id.textView9);
        refresh = findViewById(R.id.button);
        showHomeLocation = findViewById(R.id.button2);
        showHomeLocation.setVisibility(View.INVISIBLE);
        clearHomeLocation = findViewById(R.id.button3);
        clearHomeLocation.setVisibility(View.INVISIBLE);
        homeLocation = findViewById(R.id.textView2);
        homeLocation.setVisibility(View.INVISIBLE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(STATE, tracking).apply();
        currLocation = null;
        float savedLatitude = sharedPreferences.getFloat(LATITUDE,0F);
        float savedLongitude = sharedPreferences.getFloat(LONGITUDE,0F);
        if (sharedPreferences.getFloat(LATITUDE,0F) != 0F || sharedPreferences.getFloat(LONGITUDE,0F)!= 0 ){
            homeLocation.setVisibility(View.VISIBLE);
            homeLocation.setText("your home location is defined as <"+savedLatitude+","+savedLongitude+">");
        }
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (tracking) {
                    currLocation = location;
                    updateUI(location);
                    addNewHomeLocation(location);
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        configure_button();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                configure_button();
                break;
            default:
                break;
        }
    }

    private void addNewHomeLocation(final Location location){
        if (location.getAccuracy()<= MIN_ACCURAY){
            showHomeLocation.setVisibility(View.VISIBLE);
            showHomeLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sharedPreferences.edit().putFloat(LATITUDE,(float)location.getLatitude()).putFloat(LONGITUDE,(float)location.getLongitude()).apply();
                    homeLocation.setVisibility(View.VISIBLE);
                    homeLocation.setText("your home location is defined as <"+location.getLatitude()+" , "+location.getLongitude()+">");
                   handleClearButton(clearHomeLocation);
                }
            });

        }
        else {
            showHomeLocation.setVisibility(View.INVISIBLE);
        }
    }

    private void handleClearButton(final Button button){
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.edit().putFloat(LATITUDE,0F).putFloat(LONGITUDE,0F).apply();
                homeLocation.setText("");
                button.setVisibility(View.INVISIBLE);
            }
        });
    }

    void configure_button() {
        // first check for permissions

        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        refresh.setOnClickListener(new View.OnClickListener() {
            boolean clicked = false;

            @Override
            public void onClick(View view) {
                clicked = !clicked;
                if (clicked) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                            Manifest.permission.ACCESS_FINE_LOCATION}
                                    , REQUEST_CODE);
                        }
                        return;
                    } else {
                        if (currLocation != null){
                            updateUI(currLocation);
                        }
                        else {
                            locationManager.requestLocationUpdates("gps", REFRESH_TIME, 0, listener);
                        }
                    }
                    refresh.setText("Stop Tracking");
                    tracking = true;

                } else {
                    tracking = false;
                    refresh.setText("Start Tracking");
                    handleClearButton(clearHomeLocation);
                    resetUI();
                }

            }
        });
    }


    private void resetUI() {
        showHomeLocation.setVisibility(View.INVISIBLE);
        longitude.setText("");
        latitude.setText("");
        accuracy.setText("");
    }

    private void updateUI(Location location) {
        longitude.setText(String.valueOf(location.getLongitude()));
        latitude.setText(String.valueOf(location.getLatitude()));
        accuracy.setText(String.valueOf(location.getAccuracy()));
    }
}
