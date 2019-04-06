package com.example.psquared;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeTalker extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_talker);

        //Wait for talker button to be clicked
        onTalk();
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
                talkBtn.setAlpha(.5f);
                talkBtn.setText("Connecting to a Listener...");
                talkBtn.setTextSize(30);
                tv.setVisibility(View.VISIBLE);
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
