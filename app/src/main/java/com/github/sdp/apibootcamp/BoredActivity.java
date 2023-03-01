package com.github.sdp.apibootcamp;

import android.accounts.NetworkErrorException;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class BoredActivity {
    private BoredAPI api = BoredAPI.createAPI();
    private String activity;
    private String type;
    private int participantCount;
    private double price;
    private String link;
    private int key;
    private int accessibilityLevel;



    public String getActivity() {
        return activity;
    }

    public String getType() {
        return type;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public double getPrice() {
        return price;
    }

    public String getLink() {
        return link;
    }

    public int getAccessibilityLevel() {
        return accessibilityLevel;
    }
}
