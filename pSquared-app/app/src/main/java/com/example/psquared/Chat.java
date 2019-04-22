package com.example.psquared;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.format.DateFormat;

import com.firebase.client.collection.LLRBNode;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


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

    }

    private void displayChatMessage() {
        ListView listofMessage = (ListView)findViewById(R.id.list_of_message);
        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,R.layout.list_item,FirebaseDatabase.getInstance().getReference(id)) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView name, message;
                name = (TextView) v.findViewById(R.id.username);
                message = (TextView) v.findViewById(R.id.message);
                message.setText(model.getMessageText());
                String me = settings.getString("name", "unknown");
                RelativeLayout.LayoutParams shiftName = (RelativeLayout.LayoutParams) name.getLayoutParams();
                RelativeLayout.LayoutParams shiftText = (RelativeLayout.LayoutParams) message.getLayoutParams();
                if (model.getMessageUser().equals(me)) {
                    name.setText(me);
                    name.setTextColor(Color.WHITE);
                    message.setTextColor(Color.WHITE);
                    shiftName.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
                    shiftText.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
                } else {
                    name.setText("Anonymous Penguin");
                    name.setTextColor(Color.RED);
                    message.setTextColor(Color.RED);
                    shiftName.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
                    shiftText.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
                }

            }
        };
        listofMessage.setAdapter(adapter);
    }

}
