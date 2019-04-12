package com.example.psquared;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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

    public void toggleListener(View view) {
        EditText editText = findViewById(R.id.editText);
        String username = editText.getText().toString();
        DatabaseReference users = database.getReference("Users");
        final DatabaseReference child = users.child(username);
        child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //getting the listener value from the firebase database
                long value = dataSnapshot.getValue(long.class);
                Integer v = new Integer((int) value);
                if (v == 0) {
                    child.setValue(1);
                } else if (v == 1) {
                    child.setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
