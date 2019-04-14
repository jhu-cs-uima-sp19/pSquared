package com.example.psquared;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CounselorMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counselor_main);
    }

    public void launchListeners(View view) {
        Intent mainToListeners = new Intent(this, ListenerDatabase.class);
        startActivity(mainToListeners);
    }

    public void toSettings(View view) {
        Intent toSettings = new Intent (this, Settings.class);
        startActivity(toSettings);
    }

    public void launchStatistics(View view) {
        Intent maintoStats = new Intent(this, Statistics.class);
        startActivity(maintoStats);
    }
}
