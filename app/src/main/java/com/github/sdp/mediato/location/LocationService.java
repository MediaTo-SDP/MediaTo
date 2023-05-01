package com.github.sdp.mediato.location;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.github.sdp.mediato.R;
import com.github.sdp.mediato.data.UserDatabase;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationService extends Service {
    public static String ACTION_START_LOCATION_SERVICE = "startLocationService";
    private String username;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();
                UserDatabase.updateLocation(username, latitude, longitude);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startLocationService() {
        LocationRequest locationRequest = new LocationRequest.Builder(100)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setIntervalMillis(100)
                .setMinUpdateIntervalMillis(0)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Permission already checked
            return;
        }
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null && intent.getAction().equals(ACTION_START_LOCATION_SERVICE)) {
            this.username = intent.getStringExtra("username");
            startLocationService();
        }
        return super.onStartCommand(intent, flags, startId);
    }
};
