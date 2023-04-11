package com.github.sdp.mediato.services.locationService;

import android.location.Location;

import java.util.concurrent.CompletableFuture;

import kotlinx.coroutines.flow.Flow;

public interface LocationClient {
    public CompletableFuture<Location> getLocationUpdates(double delta);
}
