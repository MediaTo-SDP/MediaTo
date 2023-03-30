package com.github.sdp.mediato;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.CompletableFuture;

public class FirebaseBootcamp extends AppCompatActivity {

  private final DatabaseReference db = FirebaseDatabase.getInstance().getReference();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_firebase_bootcamp);
  }

  public void get(View view) {
    TextView email = (TextView) findViewById(R.id.email_text);
    TextView phone = (TextView) findViewById(R.id.phone_text);
    CompletableFuture<String> future = new CompletableFuture<>();
    db.child(phone.getText().toString()).get()
        .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
          @Override
          public void onSuccess(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() == null) {
              future.completeExceptionally(new NoSuchFieldException());
            } else {
              future.complete(dataSnapshot.getValue().toString());
            }
          }
        }).addOnFailureListener(
            new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                future.completeExceptionally(e);
              }
            }
        );
    future.thenAccept(
        value -> email.setText(value)
    );
  }

  public void set(View view) {
    String email = ((TextView) findViewById(R.id.email_text)).getText().toString();
    String phone = ((TextView) findViewById(R.id.phone_text)).getText().toString();
    db.child(phone).setValue(email);
  }

}