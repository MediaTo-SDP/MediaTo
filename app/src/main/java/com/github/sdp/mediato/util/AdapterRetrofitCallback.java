package com.github.sdp.mediato.util;

import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class AdapterRetrofitCallback<T> implements Callback<T> {
    private final CompletableFuture<T> future;

    public AdapterRetrofitCallback(CompletableFuture<T> future) {
        this.future = future;
    }

    @Override @EverythingIsNonNull
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            future.complete(response.body());
        } else {
            future.completeExceptionally(new HttpException(response));
        }
    }

    @Override @EverythingIsNonNull
    public void onFailure(Call<T> call, Throwable t) {
        future.completeExceptionally(t);
    }
}
