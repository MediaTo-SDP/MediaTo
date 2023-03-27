package com.github.sdp.mediato.utility.adapters;

import android.accounts.NetworkErrorException;

import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

/**
 * Adapter class from Callback provided in the retrofit package to completable Futures
 * @param <T>
 */
public class AdapterRetrofitCallback<T> implements Callback<T> {
    private final CompletableFuture<T> future;

    /**
     * Default constructor
     * @param future the future that replace the callback system
     */
    public AdapterRetrofitCallback(CompletableFuture<T> future) {
        this.future = future;
    }

    /**
     * Called when the retrofit call responded correctly
     * @param call the outgoing call
     * @param response the incoming response
     */
    @Override @EverythingIsNonNull
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            future.complete(response.body());
        } else {
            future.completeExceptionally(new HttpException(response));
        }
    }

    /**
     * Called when the call encountered an error
     * @param call the outgoing call
     * @param t the encountered error
     */
    @Override @EverythingIsNonNull
    public void onFailure(Call<T> call, Throwable t) {
        future.completeExceptionally(new NetworkErrorException(t.getCause()));
    }
}
