package com.example.doggarden.utils;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.doggarden.CallBacks.CallBack_Location;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MyLocation {

    public static final int PERMISSION_LOCATION = 44;
    private boolean locationAccepted = true;
    private Activity activity;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private double lat=0, lng=0;
    private Location location;

    private CallBack_Location callBack_location;

    public void setCallBack_location(CallBack_Location callBack_location){
        this.callBack_location =callBack_location;
    }

    public MyLocation() {
    }

    public MyLocation(Activity activity) {
        this.activity = activity;
        getLocation();
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            location = locationResult.getLocations().get(0);
            lng = location.getLongitude();
            lat = location.getLatitude();
            if(callBack_location != null){
                callBack_location.startLocation(lat,lng);
            }
        }
    };


    public void getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    location = task.getResult();

                    locationRequest = LocationRequest.create();
                    locationRequest.setInterval(4000);
                    locationRequest.setFastestInterval(2000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    checkSettingsAndStartLocationUpdates();

                }
            });
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
        }


    }


    private void checkSettingsAndStartLocationUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(activity);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);

        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdated();
            }
        });

        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(activity, 1001);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

    }


    private void startLocationUpdated() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);

    }

    public void setLocationAccepted(boolean locationAccepted) {
        this.locationAccepted = locationAccepted;
    }

    public double getLat() {
        if (locationAccepted)
            return lat;
        return 0;
    }

    public double getLng() {
        if (locationAccepted)
            return lng;
        return 0;
    }
}
