package com.example.psquared;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeListener extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Wait for talker or listen buttons to be clicked
        onTalk();
        onListen();
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
                talkBtn.setAlpha(.5f);
                talkBtn.setText("Connecting to a Listener...");
                talkBtn.setTextSize(30);
                tv.setVisibility(View.VISIBLE);

                //Reset any settings for the listen button
                resetListen();
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
                listenBtn.setAlpha(.5f);
                listenBtn.setText("Connecting to a Talker...");
                listenBtn.setTextSize(30);
                tv.setVisibility(View.VISIBLE);

                //Reset any settings for the talk button
                resetTalk();
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
