package com.github.sdp.mediato.location;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

public class LocationHelper {
    public static String USERNAME;

    public static void startTrackingLocation(Context context, Activity activity, ActivityResultLauncher<String> requestPermissionLauncher, String username) {
        USERNAME = username;
        if(ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
        } else {
            if(!isLocationServiceRunning(activity)) {
                startLocationService(activity);
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

    /**
     * Starts the location service and passes the username to it
     * @param activity the activity that is calling this method
     */
    public static void startLocationService(Activity activity) {
        if(!isLocationServiceRunning(activity)) {
            Intent intent = new Intent(activity, LocationService.class);
            intent.putExtra("username", USERNAME);
            intent.setAction(LocationService.ACTION_START_LOCATION_SERVICE);
            activity.startService(intent);
        }
    }

}
