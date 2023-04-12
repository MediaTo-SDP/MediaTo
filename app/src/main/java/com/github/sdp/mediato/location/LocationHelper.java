package com.github.sdp.mediato.location;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.sdp.mediato.MainActivity;

import javax.annotation.Nonnull;

public class LocationHelper {

    public static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    public static void startTrackingLocation(Context context, Activity activity) {
        if(ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
        ) !=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            startLocationService();
        }
    }

    }

    private static boolean isLocationServiceRunning() {
        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null) {
            for(ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
                if(LocationService.class.getName().equals(service.service.getClassName())) {
                    if(service.foreground) return true;
                }
            }
            return false;
        }
        return false;
    }

    public static void startLocationService(Activity activity ) {
        if(!isLocationServiceRunning()) {
            Intent intent = new Intent(activity, LocationService.class);
            intent.setAction(LocationService.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location service started", Toast.LENGTH_SHORT).show();
        }
    }

}
