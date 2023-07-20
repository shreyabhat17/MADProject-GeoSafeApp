package com.example.myapplication;

import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;


import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


public class MyLocation extends AppCompatActivity implements OnMapReadyCallback {
    private TextView AddressText;
    private Button LocationButton;
    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private Marker currentLocationMarker;
    private static final int SMS_PERMISSION_REQUEST_CODE = 123;
    String s;

    private void initializeMapView() {
        if (googleMap != null) {
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);

            // Set the default location to the user's current location
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //If the permission is granted, the code inside the if block is executed
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override //onSuccess to check if location is successfully obtained.
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                        }
                    }
                });
                LocationCallback locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null) {
                            Location location = locationResult.getLastLocation();
                            // the code retrieves the last known location from the locationResult using getLastLocation()
                            if (location != null) {
                                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                updateCurrentLocationMarker(currentLocation);//update the marker on the map with the new location.
                            }
                        }
                    }
                };

                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setInterval(5000);
                //method is used to set the interval at which location updates should be received
                locationRequest.setFastestInterval(3000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            } else {
                // Request the ACCESS_FINE_LOCATION permission if it is not granted
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }
    private void updateCurrentLocationMarker(LatLng latLng) {
        if (currentLocationMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title("Current Location");
            //currentLocationMarker object of marker
            currentLocationMarker = googleMap.addMarker(markerOptions);
        } else {
            currentLocationMarker.setPosition(latLng);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location);

        AddressText = findViewById(R.id.addressText);
        LocationButton = findViewById(R.id.locationButton);

        // Initialize the Google Maps SDK with my API key
        MapsInitializer.initialize(getApplicationContext());
        // ensures that the necessary resources and services for Google Maps are properly set up.
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);//These methods handle the initialization and loading of the map in the background.
        mapView.getMapAsync(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        LocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(MyLocation.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        if (isGPSEnabled()) {
                            LocationServices.getFusedLocationProviderClient(MyLocation.this)
                                    .requestLocationUpdates(locationRequest, new LocationCallback() {
                                        @Override
                                        public void onLocationResult(@NonNull LocationResult locationResult) {
                                            super.onLocationResult(locationResult);
                                            LocationServices.getFusedLocationProviderClient(MyLocation.this)
                                                    .removeLocationUpdates(this);

                                            if (locationResult != null && locationResult.getLocations().size() > 0) {
                                                int index = locationResult.getLocations().size() - 1;
                                                double latitude = locationResult.getLocations().get(index).getLatitude();
                                                double longitude = locationResult.getLocations().get(index).getLongitude();
                                                Bundle extras = getIntent().getExtras();
                                                s = extras.getString("ENUM");
                                                AddressText.setText("Latitude: " + latitude + "\n" + "Longitude: " + longitude);
                                                SmsSender smsSender = new SmsSender();
                                                String phoneNumber = s;
                                                String mapLink = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
                                                String message = "I am at" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude + "Link: " + mapLink;
                                                // Check if the SEND_SMS permission is granted
                                                if (ContextCompat.checkSelfPermission(MyLocation.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                                                    // Permission is not granted, request it
                                                    ActivityCompat.requestPermissions(MyLocation.this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
                                                } else {
                                                    // Permission is already granted, send the SMS
                                                    smsSender.sendSms(phoneNumber, message);
                                                    Toast.makeText(MyLocation.this, "Message sent", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    }, Looper.getMainLooper());

                        } else {
                            turnOnGPS();
                        }

                    } else {
                        // Request the ACCESS_FINE_LOCATION permission
                        ActivityCompat.requestPermissions(MyLocation.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        initializeMapView();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        updateCurrentLocationMarker(currentLocation);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    // Handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, initialize the map view
                initializeMapView();
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
            }
        }
    }


    private void turnOnGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                resolvable.startResolutionForResult(MyLocation.this, LocationRequest.PRIORITY_HIGH_ACCURACY);
                            } catch (IntentSender.SendIntentException | ClassCastException ignored) {
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }




}
