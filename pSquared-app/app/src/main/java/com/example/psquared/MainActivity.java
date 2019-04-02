package com.example.psquared;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void settings(View v) {
        Intent toSettings = new Intent(this, Settings.class);
        startActivity(toSettings);
    }
    public void about(View v) {
        Intent toAbout = new Intent(this, About.class);
        startActivity(toAbout);
    }
}
