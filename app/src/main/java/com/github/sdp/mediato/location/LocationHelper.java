package com.github.sdp.mediato.location;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.sdp.mediato.MainActivity;

import javax.annotation.Nonnull;

public class LocationHelper {
    public static String USERNAME;


    public static void startTrackingLocation(Context context, Activity activity, ActivityResultLauncher<String> requestPermissionLauncher, String username) {
        Log.d("Location", "Entering startTrackingLocation");
        USERNAME = username;
        if(ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Location", "Launch request permission for coarse");
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            Log.d("Location", "start location service because permission granted");
            if(!isLocationServiceRunning(activity)) {
                Log.d("Location", "preparing to start service");
                startLocationService(activity);
            } else {
                Log.d("Location", "service already running - nothing to do ");
            }
        }
    }

    private static boolean isLocationServiceRunning(Activity activity) {
        ActivityManager activityManager =
                (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
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

    public static void startLocationService(Activity activity) {
        if(!isLocationServiceRunning(activity)) {
            Log.d("Location", "Location service not running yet in start location");
            Intent intent = new Intent(activity, LocationService.class);
            intent.putExtra("username", USERNAME);
            intent.setAction(LocationService.ACTION_START_LOCATION_SERVICE);
            activity.startService(intent);
            Toast.makeText(activity, "Location service started", Toast.LENGTH_SHORT).show();
        }
    }

}
