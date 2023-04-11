package com.github.sdp.mediato.services.locationService;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.location.LocationManager;
import android.os.Looper;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;

import java.util.concurrent.CompletableFuture;

import kotlinx.coroutines.flow.Flow;

public class LocationHelper implements LocationClient {

    private Context context;
    private FusedLocationProviderClient locationProviderClient;

    @Override
    public CompletableFuture<Location> getLocationUpdates(long delta) {
        CompletableFuture<Location> future = new CompletableFuture<>();
        if (!locationPermissionGranted()) {
            future.completeExceptionally(new Exception("Missing location permission"));
        }

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!gpsEnabled(locationManager))
            future.completeExceptionally(new Exception("GPS is disabled"));
        if (!networkEnabled(locationManager))
            future.completeExceptionally(new Exception("Network is disabled"));

        LocationRequest locationRequest = new LocationRequest.Builder(delta)
                .setIntervalMillis(delta)
                .build();
        LocationCallback callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null || locationResult.getLastLocation() == null) {
                    future.completeExceptionally(new RuntimeException("Unable to get location"));
                } else {
                    future.complete(locationResult.getLastLocation());
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Problem with permissions");
        }
        locationProviderClient.requestLocationUpdates(
                locationRequest,
                callback,
                Looper.getMainLooper()
        );
        wait(
                client.
        );
        return future;
    }

    private boolean gpsEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean networkEnabled (LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    private boolean locationPermissionGranted(){
        return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;
    }
}
