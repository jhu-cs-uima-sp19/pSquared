package com.example.psquared;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class CounselorMain extends AppCompatActivity {

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counselor_main);
        settings = getDefaultSharedPreferences(this);
        editor = settings.edit();
        editor.putBoolean("isCounselor", false);
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
