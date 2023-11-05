package com.example.myapplication;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.Manifest;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.view.View;
import android.util.Log;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private LocationManager locationManager;
    private LocationListener locationListener;
    private GoogleMap mMap;
    private TextView latitudeTextView, longitudeTextView;
    private TextView lastUpdateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitudeTextView = findViewById(R.id.latitudeTextView);
        longitudeTextView = findViewById(R.id.longitudeTextView);
        lastUpdateTextView = findViewById(R.id.lastUpdateTextView);

        Button startUpdateButton = findViewById(R.id.startUpdateButton);
        startUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dapatkan waktu saat ini
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                // Setel waktu saat ini ke TextView lastUpdateTextView
                lastUpdateTextView.setText("Last update: " + currentTime);
            }
        });

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        } else {
            // Permissions are granted, initialize location services
            initializeLocationServices();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void initializeLocationServices() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        // Permissions are already checked
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // You can add some actions here if needed after the map is ready
    }

    private class MyLocationListener implements android.location.LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("Location", "Lat: " + location.getLatitude() + " Lon: " + location.getLongitude());
            // Pastikan mMap tidak null
            if (mMap != null) {
                // Update TextViews with latitude and longitude
                latitudeTextView.setText(" Lat: "  + String.valueOf(location.getLatitude()));
                longitudeTextView.setText(" Lon: "  + String.valueOf(location.getLongitude()));

                // Create a LatLng object for the current location
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                // Clear the previous location marker if you only need one marker at a time
                mMap.clear();

                // Show the current location marker on the map
                mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                // Move the camera to the current location with a zoom level, for example, zoom level 15
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                // Show a toast message
                Toast.makeText(getBaseContext(),"GPS Capture",Toast.LENGTH_LONG).show();
            }
        }


        // Add other necessary overrides for LocationListener
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }
    }

    // Add the onRequestPermissionsResult callback here
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, initialize location services
                initializeLocationServices();
            } else {
                // Permissions denied, show a message to the user
                Toast.makeText(this, "Location permissions are necessary for this app to work.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
