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

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class HomeListener extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference availableTalkers;
    private DatabaseReference availableListeners;
    private DatabaseReference curUserTalker;
    private DatabaseReference curUserListener;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    static boolean availableAsTalker;
    static boolean availableAsListener;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_listener);

        availableAsTalker = false;
        availableAsListener = false;

        //Wait for talker or listen buttons to be clicked
        onTalk();
        onListen();

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
        availableListeners = database.getReference("availableListeners");
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
     * Listener presses the Talk button.
     */
    public void onTalk() {
        final Button talkBtn = findViewById(R.id.talk);
        final TextView tv = findViewById(R.id.pressToStopTalk);

        talkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!availableAsTalker) {
                    talkBtn.setAlpha(.5f);
                    talkBtn.setText("Connecting to a Listener...");
                    talkBtn.setTextSize(30);
                    tv.setVisibility(View.VISIBLE);

                    //changing firebase database values
                    curUserTalker = availableTalkers.child(email.substring(0, email.indexOf("@")));
                    curUserTalker.setValue(email);

                    //changing boolean value to tell program button is selected
                    availableAsTalker = true;
                } else {

                    //remove yourself from available talkers list
                    curUserTalker.removeValue();
                    availableAsTalker = false;
                    resetTalk();
                }
            }
        });
    }


    /**
     * Reset button attributes when Talk button is "unclicked".
     */
    public void resetTalk() {
        final Button talkBtn = findViewById(R.id.talk);
        final TextView tv = findViewById(R.id.pressToStopTalk);

        talkBtn.setAlpha(1f);
        talkBtn.setText("Talk");
        talkBtn.setTextSize(55);
        tv.setVisibility(View.GONE);

    }

    /**
     * Listener presses the Listen button.
     */
    public void onListen() {
        final Button listenBtn = findViewById(R.id.listen);
        final TextView tv = findViewById(R.id.pressToStopListen);

        listenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!availableAsListener) {
                    listenBtn.setAlpha(.5f);
                    listenBtn.setText("Connecting to a Talker...");
                    listenBtn.setTextSize(30);
                    tv.setVisibility(View.VISIBLE);//changing firebase database values

                    curUserListener = availableListeners.child(email.substring(0, email.indexOf("@")));
                    curUserListener.setValue(email);

                    //changing boolean value to tell program button is selected
                    availableAsListener = true;

                } else {
                    //remove yourself from availableListenersList
                    curUserListener.removeValue();
                    resetListen();
                    availableAsListener = false;
                }
            }
        });
    }

    /**
     * Reset button attributes when Listen button is "unclicked".
     */
    public void resetListen() {
        final Button listenBtn = findViewById(R.id.listen);
        final TextView tv = findViewById(R.id.pressToStopListen);

        listenBtn.setAlpha(1f);
        listenBtn.setText("Listen");
        listenBtn.setTextSize(55);
        tv.setVisibility(View.GONE);
    }

    //Stuff I'm just trying out for more efficient code-writing.
    //May not use this part.
    public void onClick(boolean isTalk) {
        //Locate both buttons
        final Button talkBtn = findViewById(R.id.talk);
        final Button listenBtn = findViewById(R.id.listen);

        //Change button attributes
        talkBtn.setAlpha(0.5f);
        listenBtn.setAlpha(0.5f);
        if (isTalk) {
            talkBtn.setText("Connecting to a Listener...");
            talkBtn.setTextSize(30);
        } else {
            listenBtn.setText("Connecting to a Talker...");
            listenBtn.setTextSize(30);
        }
    }
}