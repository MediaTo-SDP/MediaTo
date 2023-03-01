package com.github.sdp.apibootcamp;

import android.accounts.NetworkErrorException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public interface BoredAPI {
    @GET("activity")
    Call<BoredActivity> getActivity();
    static BoredAPI createAPI(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.boredapi.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(BoredAPI.class);
    }


}
