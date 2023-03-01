package com.github.sdp.apibootcamp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.accounts.NetworkErrorException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.sdp.apibootcamp.Storage.ActivityDatabase;
import com.github.sdp.apibootcamp.Storage.StoredActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private BoredAPI api = BoredAPI.createAPI();
    private ActivityDatabase activityDB = Room.databaseBuilder(getApplicationContext(),
            ActivityDatabase.class, "offlineDB").build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClick(View v) {
        getActivity();
    }

    private void updateView(String text) {
        TextView activityText = (TextView) findViewById(R.id.boredText);
        activityText.setText(text);
    }


    private void getActivity() {
        api.getActivity().enqueue(new Callback<BoredActivity>() {
            @Override
            public void onResponse(Call<BoredActivity> call, Response<BoredActivity> response) {
                if (!response.isSuccessful()) {
                    this.onFailure(null, new NetworkErrorException("Unable to contact api"));
                    return;
                }
                String receivedActivity = response.body().getActivity();
                activityDB.getDAO().insert(new StoredActivity(receivedActivity));
                updateView(receivedActivity);
            }

            @Override
            public void onFailure(Call<BoredActivity> call, Throwable t) {
                List<StoredActivity> activities = activityDB.getDAO().getStoredActivities();
                String activityText = activities.get((int)(Math.random() * activities.size()))
                        .activity;

            }
        });
    }
}