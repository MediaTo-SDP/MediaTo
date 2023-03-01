package com.github.sgueissa.bootcamp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.sgueissa.bootcamp.databinding.ActivityFirebaseBootcampBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.CompletableFuture;

public class FirebaseBootcamp extends AppCompatActivity {

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private ActivityFirebaseBootcampBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFirebaseBootcampBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    public void get(View view){
        CompletableFuture<String> future = new CompletableFuture<>();
        db.child(binding.phoneText.getText().toString()).get().addOnSuccessListener(
            dataSnapshot-> {
                if (dataSnapshot.getValue() == null) future.completeExceptionally(new NoSuchFieldException());
                else future.complete(dataSnapshot.getValue().toString());
            }).addOnFailureListener(e->future.completeExceptionally(e));

        future.thenAccept(value -> binding.emailText.setText(value));
    }

    public void set(View view){
        db.child(binding.phoneText.getText().toString())
                .setValue(binding.emailText.getText().toString());
    }

}