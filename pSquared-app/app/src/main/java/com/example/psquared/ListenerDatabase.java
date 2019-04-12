package com.example.psquared;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public void setListener(View view) {
        DatabaseReference users = database.getReference("Users");
        DatabaseReference child = users.child("charles");
        child.setValue(1);
    }
}
