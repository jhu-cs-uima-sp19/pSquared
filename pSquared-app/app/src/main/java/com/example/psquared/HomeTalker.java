package com.example.psquared;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

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

    //TIMER
    //Components for installing a timer that appears at button click
    /*TextView timerTV = findViewById(R.id.timerText);
    int secondsPassed = 0;
    Timer myTimer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            secondsPassed++;
            timerTV.setText(secondsPassed);
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_talker);
        availableAsTalker = false;
        availableAsListener = false;
        //Wait for talker button to be clicked
        onTalk();

        //Wait for overflow button to be clicked
        overflowClicked();
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
                Intent intent = new Intent(HomeTalker.this,Chat.class);
                startActivity(intent);
                /*if (!availableAsTalker) {
                    talkBtn.setAlpha(.5f);
                    talkBtn.setText("Connecting to a Listener...");
                    talkBtn.setTextSize(30);
                    tv.setVisibility(View.VISIBLE);

                    //TIMER
                    //myTimer.scheduleAtFixedRate(task, 1000, 1000);

                    //changing Firebase database values
                    curUser = availableTalkers.child(email.substring(0, email.indexOf("@")));
                    curUser.setValue(email);

                    //changing boolean value to tell program button is selected
                    availableAsTalker = true;

                } else {
                    curUser.removeValue();
                    resetTalk();;
                    availableAsTalker = false;
                }
                } */


            }
        });
    }

    /**
     * Overflow buttons shows pop-up menu that will lead to Settings or About pages.
     */
    public void overflowClicked() {
        final ImageButton overflowBtn = findViewById(R.id.overflow);
        overflowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getApplicationContext(), v);
                MenuInflater inflater = popup.getMenuInflater();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.settings) {
                            Intent toSettings = new Intent(getApplicationContext(), Settings.class);
                            startActivity(toSettings);
                            return true;
                        } else if (item.getItemId() == R.id.about) {
                            Intent toAbout = new Intent(getApplicationContext(), About.class);
                            startActivity(toAbout);
                            return true;
                        }
                        return false;
                    }
                });
                inflater.inflate(R.menu.overflow_menu, popup.getMenu());
                popup.show();
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
