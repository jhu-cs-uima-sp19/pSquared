package com.example.psquared;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private long startTime = -1;

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
            Context context;
/*
            @Override
            public ChatMessage getItem(int pos) {
                return super.getItem(getCount() - 1 - pos);
            }
            @Override
            public int getItemViewType(int position) {
                String me = settings.getString("name", "unknown");
                ChatMessage chat = this.getItem(position);
                if(chat.getMessageUser().equals(me)){
                    return 0;
                }else{
                    return 1;
                }
            }
            @Override
            public View getView(int position, View view, ViewGroup viewGroup) {
                LayoutInflater msgInflate = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                ChatMessage msg = this.getItem(position);
                if(this.getItemViewType(position) == 0) {
                    view = msgInflate.inflate(R.layout.my_message,null);
                } else {
                    view = msgInflate.inflate(R.layout.your_message, null);
                }
                return view;
            }*/
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView  message;
                /*if (this.getItemViewType(position) == 0) {
                    message = v.findViewById(R.id.my_message_text);
                } else {
                    message = v.findViewById(R.id.your_message_text);
                }*/
                message = v.findViewById(R.id.mymessage);
                message.setText(model.getMessageText());
            }
        };
        listofMessage.setAdapter(adapter);
    }

}
