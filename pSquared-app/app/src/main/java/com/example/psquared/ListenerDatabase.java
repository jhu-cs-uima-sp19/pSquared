package com.example.psquared;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListenerDatabase extends AppCompatActivity {
    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getApplicationContext());
        setContentView(R.layout.activity_listener_database);
        database = FirebaseDatabase.getInstance();
    }
    public void backButton(View view) {
        Intent listenersToMain = new Intent(this, CounselorMain.class);
        startActivity(listenersToMain);
    }

    public void addListener(View view) {
        EditText editText = findViewById(R.id.editText);
        final String username = editText.getText().toString();
        DatabaseReference users = database.getReference("Users");
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    if (username.equals(s.getKey())) {
                        final DatabaseReference child = database.getReference().child("Users").child(username);
                        child.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //getting the listener value from the firebase database
                                long value = dataSnapshot.getValue(long.class);
                                Integer v = new Integer((int) value);
                                if (v == 0) {
                                    child.setValue(1);
                                    Toast.makeText(getApplicationContext(), "Successfully converted to listener", Toast.LENGTH_SHORT).show();
                                } else if (v == 1) {
                                    Toast.makeText(getApplicationContext(), "User is already listener", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void removeListener(View view) {
        EditText editText = findViewById(R.id.editText);
        final String username = editText.getText().toString();
        final DatabaseReference users = database.getReference("Users");
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    if (username.equals(s.getKey())) {
                        final DatabaseReference child = database.getReference().child("Users").child(username);
                        child.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //getting the listener value from the firebase database
                                long value = dataSnapshot.getValue(long.class);
                                Integer v = new Integer((int) value);
                                if (v == 0) {
                                    Toast.makeText(getApplicationContext(), "User is already talker", Toast.LENGTH_SHORT).show();
                                } else if (v == 1) {
                                    child.setValue(0);
                                    Toast.makeText(getApplicationContext(), "Successfully converted to talker", Toast.LENGTH_SHORT).show();
                                }
                                child.removeEventListener(this);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
