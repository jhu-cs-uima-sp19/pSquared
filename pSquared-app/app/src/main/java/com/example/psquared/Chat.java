package com.example.psquared;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class Chat extends AppCompatActivity {

    private FirebaseListAdapter<ChatMessage> adapter;
    RelativeLayout activity_chat;
    FloatingActionButton send;
    FloatingActionButton exit;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    private DatabaseReference chat;
    private String id;
    long startTime = -1;
    long latestTime = -1;

    private boolean backexit = false;
    @Override
    public void onBackPressed() {
        if (backexit) {
            chat = FirebaseDatabase.getInstance().getReference(id);
            chat.removeValue();
            finish();
        } else {
            Toast.makeText(this, "Press Back again to Leave the Chat.",
                    Toast.LENGTH_SHORT).show();
            backexit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backexit = false;
                }
            }, 10 * 1000);
        }
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        if (date1 == null) {
            return 0;
        }
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        // assign unique id to the talker and listener
        settings = getDefaultSharedPreferences(this);
        editor = settings.edit();
        id = settings.getString("curChat", "fail");
        // initialize necessary objects
        activity_chat = (RelativeLayout)findViewById(R.id.activity_chat);
        send = (FloatingActionButton)findViewById(R.id.fabsend);
        exit = (FloatingActionButton)findViewById(R.id.fabexit);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText)findViewById(R.id.input);
                FirebaseDatabase.getInstance().getReference(id).push().setValue(new ChatMessage(input.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                input.setText("");
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat = FirebaseDatabase.getInstance().getReference(id);
                chat.removeValue();
                finish();
            }
        });
        
        Snackbar.make(activity_chat, "Welcome " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT).show();
        //load content
        displayChatMessage();
        chat = FirebaseDatabase.getInstance().getReference(id);
        chat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Snackbar.make(activity_chat, "Goodbye", Snackbar.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


    }

    private void displayChatMessage() {
        ListView listofMessage = (ListView)findViewById(R.id.list_of_message);
        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,R.layout.list_item,FirebaseDatabase.getInstance().getReference(id)) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messageText,messageUser,messageTime;
                messageText = (TextView) v.findViewById(R.id.message_text);
                messageUser = (TextView) v.findViewById(R.id.message_user);
                messageTime = (TextView) v.findViewById(R.id.message_time);
                String me = settings.getString("name", "unknown");

                if (model.getMessageUser().equals(me)) {
                    messageUser.setText(me);
                } else {
                    messageUser.setText("Anonymous");
                }

                messageText.setText(model.getMessageText());

                if (startTime == -1) {
                    startTime = model.getMessageTime();
                    latestTime = startTime;
                } else {
                    latestTime = model.getMessageTime();
                    TimeUnit minutes;
                    long dif = latestTime - startTime;
                    long min = TimeUnit.MILLISECONDS.toMinutes(dif);
                    if (min > 1) {
                        messageUser.setText("Penguins");
                    }
                }

                //messageTime.setText(""+getDateDiff(startTime,latestTime,TimeUnit.SECONDS));
                //messageTime.setText(DateFormat.format("(HH:mm)",model.getMessageTime()));
            }
        };
        listofMessage.setAdapter(adapter);
    }

}
