// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.github.sgueissa.bootcamp;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
public class MapsMarkerActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps_marker);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng epfl = new LatLng(46.520536, 6.568318);
        LatLng sat = new LatLng(46.520544, 6.567825) ;
        googleMap.addMarker(new MarkerOptions()
                .position(sat)
                .snippet("Good beer for cheap")
                .title("Satellite"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(epfl, 15f));

        // Instantiates a new CircleOptions object and defines the center and radius
        CircleOptions circleOptions = new CircleOptions()
                .center(sat)
                .radius(200)
                .strokeColor(Color.GREEN)
                .fillColor(Color.argb(128, 0, 255, 0))
                .clickable(true);


        // Get back the mutable Circle
        Circle circle = googleMap.addCircle(circleOptions);
        circle.setTag("SafeArea");


        googleMap.setOnCircleClickListener((GoogleMap.OnCircleClickListener) circle1 -> {
            // Flip the r, g and b components of the circle's stroke color.
            if(circle1.getTag().equals("SafeArea")) {
                Toast.makeText(MapsMarkerActivity.this, "It is the safe area, don't go too far away from SAT!", Toast.LENGTH_SHORT).show();
            }
        });


        // adding on click listener to marker of google maps.
        googleMap.setOnMarkerClickListener(marker -> {
            // on marker click we are getting the title of our marker
            // which is clicked and displaying it in a toast message.
            String markerName = marker.getTitle();
            Toast.makeText(MapsMarkerActivity.this, "Clicked location is " + markerName, Toast.LENGTH_SHORT).show();
            return false;
        });
    }
}

