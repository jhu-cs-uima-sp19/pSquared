package com.example.psquared;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class HomeTalker extends AppCompatActivity {

    static boolean availableAsTalker;
    static boolean availableAsListener;
    private FirebaseDatabase database;
    private DatabaseReference availableTalkers;
    private DatabaseReference availableListeners;
    private DatabaseReference curUser;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_talker);
        availableAsTalker = false;
        availableAsListener = false;
        //Wait for talker button to be clicked
        onTalk();
    }

    @Override
    protected void onStart() {
        super.onStart();
        settings = getDefaultSharedPreferences(this);
        editor = settings.edit();
        email = settings.getString("email", "email");
        if (email.equals("email")) {
            Toast.makeText(getApplicationContext(), "data error: email storage error", Toast.LENGTH_SHORT).show();
        }

        database = FirebaseDatabase.getInstance();
        availableTalkers = database.getReference("availableTalkers");
    }

    /**
     * Talker presses the Talk button.
     */
    public void onTalk() {
        final Button talkBtn = findViewById(R.id.talk);
        final TextView tv = findViewById(R.id.pressToStopText);
        talkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!availableAsTalker) {
                    talkBtn.setAlpha(.5f);
                    talkBtn.setText("Connecting to a Listener...");
                    talkBtn.setTextSize(30);
                    tv.setVisibility(View.VISIBLE);

                    //changing firebase database values
                    curUser = availableTalkers.child(email.substring(0, email.indexOf("@")));
                    curUser.setValue(email);

                    //changing boolean value to tell program button is selected
                    availableAsTalker = true;
                } else {
                    curUser.removeValue();
                    resetTalk();;
                    availableAsTalker = false;
                }


            }
        });
    }

    /**
     * Reset button attributes when Talk button is "unclicked".
     */
    public void resetTalk() {
        final Button talkBtn = findViewById(R.id.talk);
        final TextView tv = findViewById(R.id.pressToStopText);

        talkBtn.setAlpha(1f);
        talkBtn.setText("Talk");
        talkBtn.setTextSize(55);
        tv.setVisibility(View.GONE);
    }
}
