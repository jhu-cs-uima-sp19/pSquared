package com.example.psquared;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class HomeTalker extends AppCompatActivity {

    static boolean availableAsTalker;
    private FirebaseDatabase database;
    private DatabaseReference availableTalkers;
    private DatabaseReference availableListeners;
    private DatabaseReference curUser;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private boolean canSendPushNotifs;
    private boolean noWaitingTalkers = true;

    String email;
    int count = 0;
    final Timer T = new Timer();
    int numAvailableListenersPrev = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_talker);
        availableAsTalker = false;
        settings = getDefaultSharedPreferences(this);
        editor = settings.edit();
        editor.commit();
        //Wait for talker button to be clicked
        onTalk();

        //Wait for overflow button to be clicked
        overflowClicked();

        //Timer starts to let user know how long they've been waiting
        time();
    }

    @Override
    protected void onStart() {
        super.onStart();
        settings = getDefaultSharedPreferences(this);
        editor = settings.edit();
        editor.putBoolean("canLook", true);
        editor.commit();

        email = settings.getString("email", "email");
        if (email.equals("email")) {
            Toast.makeText(getApplicationContext(), "data error: email storage error", Toast.LENGTH_SHORT).show();
        }
        database = FirebaseDatabase.getInstance();
        availableTalkers = database.getReference("availableTalkers");
        availableListeners = database.getReference("availableListeners");

        availableAsTalker = false;

        //set up push notifications
        canSendPushNotifs = true;
        pushNotifications();
    }

    public void time() {

        final TextView talkTimer = findViewById(R.id.talkTimer);

        //Count time of wait
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        long hours = TimeUnit.SECONDS.toHours(count);
                        String hourFormatted = String.format("%02d", hours);
                        long minutes = TimeUnit.SECONDS.toMinutes(count) - (hours * 60);
                        String minFormatted = String.format("%02d", minutes);
                        long seconds = TimeUnit.SECONDS.toSeconds(count) - (minutes *60);
                        String secFormatted = String.format("%02d", seconds);
                        talkTimer.setText("You have been waiting for "+
                                hourFormatted + ":" + minFormatted + ":" + secFormatted);
                        count++;
                    }
                });
            }
        }, 1000, 1000);
    }

    /**
     * Talker presses the Talk button.
     */
    public void onTalk() {
        final Button talkBtn = findViewById(R.id.talk);
        final TextView tv = findViewById(R.id.pressToStopText);
        final TextView talkTimer = findViewById(R.id.talkTimer);

        // checks to see if the talker is the first position in the queue
        final ValueEventListener queuePosChecker = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // a counter that counts the position of the available talker in the array
                //int counter = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!snapshot.getKey().equals("dummy") && snapshot.getValue().toString().equals(email)) {
                        editor.putBoolean("canLook", true);
                        editor.commit();
                        break;
                    } else if (!snapshot.getKey().equals("dummy") && !snapshot.getValue().toString().equals(email)) {
                        editor.putBoolean("canLook", false);
                        editor.commit();
                        //counter ++;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        // create listener for finding an available listener
        final ValueEventListener listen = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //loop through listeners
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // ignore dummy entry of database
                    if (!snapshot.getKey().equals("dummy") && settings.getBoolean("canLook", false)) {

                        //post chat to to database for listener to find
                        DatabaseReference chatdb = database.getReference("chats").child(snapshot.getValue().toString());
                        chatdb.setValue("meep");

                        //remove listener from available listeners
                        DatabaseReference listener = database.getReference("availaberListeners").child(snapshot.getKey());
                        listener.removeValue();

                        // remove yourself from available listeners
                        // availableListeners.child(snapshot.getKey()).removeValue();
                        // curUser.removeValue();

                        // remember chat ID for chatroom
                        editor.putString("curChat", snapshot.getValue().toString());
                        editor.putString("name", email);

                        // not available to look anymore
                        editor.putBoolean("canLook", false);
                        editor.commit();

                        resetTalk();
                        availableListeners.removeEventListener(this);
                        // go to chat
                        Intent toChat = new Intent(HomeTalker.this, Chat.class);
                        startActivity(toChat);

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        talkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Don't send any push notifications if button is clicked
                canSendPushNotifs = false;

                //reset timer
                count = 0;

                // creating database reference to list of available listeners
                //final DatabaseReference availableListeners = database.getReference("availableListeners");

                if (!availableAsTalker) {
                    talkBtn.setAlpha(.5f);
                    talkBtn.setText("Connecting to a Listener...");
                    talkBtn.setTextSize(30);
                    tv.setVisibility(View.VISIBLE);
                    talkTimer.setVisibility(View.VISIBLE);

                    //changing Firebase database values
                    curUser = availableTalkers.child(Long.toString(System.currentTimeMillis()));
                    curUser.setValue(email);

                    //changing boolean value to tell program button is selected
                    availableAsTalker = true;

                    // add listener to database reference
                    availableListeners.addValueEventListener(listen);
                    availableTalkers.addValueEventListener(queuePosChecker);

                } else {
                    curUser.removeValue();

                    // stop listening for available listeners.
                    availableListeners.removeEventListener(listen);
                    availableTalkers.removeEventListener(queuePosChecker);
                    resetTalk();
                    availableAsTalker = false;
                    //reset timer
                    count = 0;
                    //Can send push notifications again
                    canSendPushNotifs = true;
                }
            }
        });
    }

    public void pushNotifications() {

        final ValueEventListener talkerWaiting = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //loop through talkers
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // ignore dummy entry of database
                    if (!snapshot.getKey().equals("dummy")) {
                        noWaitingTalkers = false;
                        break;
                    }
                    noWaitingTalkers = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        availableTalkers.addValueEventListener(talkerWaiting);

        final ValueEventListener listenerAvailable = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int numAvailableListenerNow = 0;

                //loop through listeners
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // ignore dummy entry of database
                    if (!snapshot.getKey().equals("dummy")) {
                        //Should only increment for entries that are not dummy
                        numAvailableListenerNow++;
                    }
                }

                if (noWaitingTalkers && canSendPushNotifs && settings.getBoolean("notify", true)
                        && numAvailableListenerNow > numAvailableListenersPrev) {
                    //send notifications if above conditions are met and there are now more listeners than there were before
                    sendPushNotification();
                }
                numAvailableListenersPrev = numAvailableListenerNow;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        availableListeners.addValueEventListener(listenerAvailable);
    }

    public void sendPushNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //Create notification manager
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            //Create channel in which to send push notification
            String CHANNEL_ID = "my_channel_01";
            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);

            //Send push notification
            Notification notify = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Listener available on pSquared!")
                    .setContentText("You can now talk about your day in a pSquared chatbox with a Listener")
                    .setSmallIcon(R.drawable.psquared_logo).setChannelId(CHANNEL_ID).build();

            notify.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(0, notify);
        }
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
        final TextView talkTimer = findViewById(R.id.talkTimer);

        count = 0;
        talkBtn.setAlpha(1f);
        talkBtn.setText("Talk");
        talkBtn.setTextSize(55);
        tv.setVisibility(View.INVISIBLE);
        talkTimer.setVisibility(View.INVISIBLE);
    }

}
