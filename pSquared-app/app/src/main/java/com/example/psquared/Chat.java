package com.example.psquared;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
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
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


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
    private boolean exitChat = false;

    /* override back button */
    @Override
    public void onBackPressed() {
        if (exitChat) {
            chat = FirebaseDatabase.getInstance().getReference(id);
            chat.removeValue();
            finish();
        } else {
            Toast.makeText(this, "Press back again to leave the chat.",
                    Toast.LENGTH_SHORT).show();
            exitChat = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitChat = false;
                }
            }, 10 * 1000);
        }
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
        activity_chat = findViewById(R.id.activity_chat);
        send = findViewById(R.id.fabsend);
        exit = findViewById(R.id.fabexit);

        /* exit chat if the other person leaves */
        chat = FirebaseDatabase.getInstance().getReference(id);
        chat.push().setValue(new ChatMessage("", ""));
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
                Snackbar.make(activity_chat, "Goodbye", Snackbar.LENGTH_SHORT).show();
                finish();
            }

        });

        /* push message to firebase database */
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText)findViewById(R.id.input);
                FirebaseDatabase.getInstance().getReference(id).push().setValue(new ChatMessage(input.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                input.setText("");
            }
        });

        /* press exit button to leave */
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exitChat) {
                    chat = FirebaseDatabase.getInstance().getReference(id);
                    chat.removeValue();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Press exit again to leave the chat.", Toast.LENGTH_SHORT).show();
                    exitChat = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exitChat = false;
                        }
                    }, 10 * 1000);
                }
            }
        });
        
        //load content
        displayChatMessage();

    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void displayChatMessage() {
        ListView listofMessage = (ListView)findViewById(R.id.list_of_message);
        Query query = FirebaseDatabase.getInstance().getReference(id);

        FirebaseListOptions<ChatMessage> options = new FirebaseListOptions.Builder<ChatMessage>()
                .setLayout(R.layout.list_item)//Note: The guide doesn't mention this method, without it an exception is thrown that the layout has to be set.
                .setQuery(query, ChatMessage.class)
                .build();
        adapter = new FirebaseListAdapter<ChatMessage>(options) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messageText, other;
                String me = settings.getString("name", "unknown");
                    if (model.getMessageUser().equals(me)) {
                        messageText = v.findViewById(R.id.mymessage);
                    } else {
                        messageText = v.findViewById(R.id.yourmessage);
                    }
                    messageText.setText(model.getMessageText());
                    TextView mine = v.findViewById(R.id.mymessage);
                    if (mine.getText().toString().equals(model.getMessageText())) {
                        mine.setVisibility(View.VISIBLE);
                        other = v.findViewById(R.id.yourmessage);
                        other.setVisibility(View.GONE);
                    } else {
                        mine.setVisibility(View.GONE);
                        other = v.findViewById(R.id.yourmessage);
                        other.setVisibility(View.VISIBLE);
                    }
                    if (model.getMessageUser().equals("") && model.getMessageText().equals("")) {
                        messageText = v.findViewById(R.id.mymessage);
                        other = v.findViewById(R.id.yourmessage);
                        messageText.setVisibility(View.GONE);
                        other.setVisibility(View.GONE);
                    }
                    if (startTime == -1) {
                        startTime = model.getMessageTime();
                        latestTime = startTime;
                    } else {
                        latestTime = model.getMessageTime();
                        long mil = latestTime - startTime;
                        long min = (mil / 1000) / 60;
                        int dif = (int) min;
                        if ((dif % 30 == 0) && (dif != 0)) {
                            Snackbar.make(activity_chat, "You have been chatting for " + dif + " minutes!", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }

        };
        listofMessage.setAdapter(adapter);
    }

}
