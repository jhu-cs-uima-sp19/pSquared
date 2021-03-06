package com.example.psquared;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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
    private boolean canSendPushNotifs;
    private boolean noWaitingTalkers = true;

    String email;
    int count = 0;
    final Timer T = new Timer();
    private int numAvailableListenersPrev;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_listener);

        // setup shared preferences
        settings = getDefaultSharedPreferences(this);
        editor = settings.edit();
        email = settings.getString("email", "email");
        editor.putBoolean("canLook", true);
        editor.putBoolean("isCounselor", false);
        editor.commit();

        availableAsTalker = false;
        availableAsListener = false;
        editor.putBoolean("listening", false);
        editor.putBoolean("talking", false);
        editor.commit();

        numAvailableListenersPrev = 0;

        //Wait for talker or listen buttons to be clicked
        onTalk();
        onListen();
        //Wait for overflow button to be clicked
        overflowClicked();

        //Will make buttons change on hover
        onHoverButtons();

        //timer
        time();
    }

    @Override
    protected void onStart() {
        super.onStart();


        if (email.equals("email")) {
            Toast.makeText(getApplicationContext(), "data error: email storage error", Toast.LENGTH_SHORT).show();
        }

        database = FirebaseDatabase.getInstance();
        availableTalkers = database.getReference("availableTalkers");
        availableListeners = database.getReference("availableListeners");

        availableAsTalker = settings.getBoolean("talking", false);
        availableAsListener = settings.getBoolean("listening", false);
        //set up push notifications
        canSendPushNotifs = true;
        listenForPushNotifications();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        makeUnavailable();
        availableAsTalker = false;
        availableAsListener = false;

        //set up push notifications
        canSendPushNotifs = true;
        listenForPushNotifications();
    }

    public void time() {

        final TextView talkTimer = findViewById(R.id.talkTimer);
        final TextView listenTimer = findViewById(R.id.listenTimer);

        //Create timer to let user know how long they've been waiting
        //Timer T = new Timer();
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
                        listenTimer.setText("You have been waiting for "+
                                hourFormatted + ":" + minFormatted + ":" + secFormatted);
                        count++;
                    }
                });
            }
        }, 1000, 1000);
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
        final TextView timerText = findViewById(R.id.talkTimer);

        // checks to see if the talker is the first position in the queue
        final ValueEventListener queuePosChecker = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // a counter that counts the position of the available talker in the arry
                int counter = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!snapshot.getKey().equals("dummy") && snapshot.getValue().toString().equals(email)&& counter == 0) {
                        //
                        //
                        // Toast.makeText(getApplicationContext(), "you are the first in the queue", Toast.LENGTH_SHORT).show();
                        editor.putBoolean("canLook", true);
                        editor.commit();
                        break;
                    } else if (!snapshot.getKey().equals("dummy") && !snapshot.getValue().toString().equals(email)){
                        editor.putBoolean("canLook", false);
                        editor.commit();
                        counter ++;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        //creating listener for finding available listeners.
        final ValueEventListener listen = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //loop through listeners
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // ignore dummy entry of database
                    if (!snapshot.getKey().equals("dummy") && settings.getBoolean("canLook", false)
                            && !snapshot.getValue().toString().equals(email.substring(0,email.indexOf("@")))) {

                        //post chat to to database for listener to find
                        DatabaseReference chatdb = database.getReference("chats").child(snapshot.getValue().toString());
                        chatdb.setValue("meep");

                        //remove listener from available listeners
                        // I don't know why this is needed but the app crashes when I remove it
                        DatabaseReference listener = database.getReference("availaberListeners").child(snapshot.getKey());
                        listener.removeValue();

                        // remove yourself from available listeners
                        availableListeners.child(snapshot.getKey()).removeValue();
                        curUserTalker.removeValue();

                        // remember chat ID for chatroom
                        editor.putString("curChat", snapshot.getValue().toString());
                        editor.putString("name", email);

                        editor.putBoolean("talking", false);

                        // not available to look anymore
                        editor.putBoolean("canLook", false);
                        editor.commit();

                        resetTalk();
                        availableListeners.removeEventListener(this);
                        // go to chat
                        Intent toChat = new Intent(HomeListener.this, Chat.class);
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
                // create database reference for list of available listeners
                final DatabaseReference availableListeners = database.getReference("availableListeners");

                if (availableAsListener) {
                    Toast.makeText(getApplicationContext(), "please remove yourself from listeners queue", Toast.LENGTH_SHORT).show();
                } else {
                    if (!availableAsTalker) {
                        talkBtn.setAlpha(.5f);
                        talkBtn.setText("Connecting...");
                        talkBtn.setTextSize(20);
                        tv.setVisibility(View.VISIBLE);
                        count = 0;
                        timerText.setVisibility(View.VISIBLE);

                        //changing firebase database values
                        curUserTalker = availableTalkers.child(Long.toString(System.currentTimeMillis()));
                        curUserTalker.setValue(email);

                        //add listener to database reference
                        availableListeners.addValueEventListener(listen);
                        availableTalkers.addValueEventListener(queuePosChecker);

                        //changing boolean value to tell program button is selected
                        availableAsTalker = true;
                        editor.putBoolean("talking", true);
                        editor.commit();

                    } else {

                        //remove yourself from available talkers list
                        curUserTalker.removeValue();

                        //remove listener from database references.
                        availableListeners.removeEventListener(listen);
                        availableTalkers.removeEventListener(queuePosChecker);
                        availableAsTalker = false;
                        editor.putBoolean("talking", false);
                        editor.commit();
                        resetTalk();
                    }
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
        final TextView timerText = findViewById(R.id.talkTimer);
        count = 0; //reset waiting time to zero
        //T.cancel(); //stop timer

        talkBtn.setAlpha(1f);
        talkBtn.setText("Talk");
        talkBtn.setTextSize(50);
        tv.setVisibility(View.INVISIBLE);
        timerText.setVisibility(View.INVISIBLE);
    }

    /**
     * Listener presses the Listen button.
     */
    public void onListen() {
        final Button listenBtn = findViewById(R.id.listen);
        final TextView tv = findViewById(R.id.pressToStopListen);
        final TextView listenTimer = findViewById(R.id.listenTimer);

        //Things to execute when Listen button is clicked
        listenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (availableAsTalker) {
                    Toast.makeText(getApplicationContext(), "please remove yourself from talkers queue", Toast.LENGTH_SHORT).show();
                } else {
                    if (!availableAsListener) {
                        listenBtn.setAlpha(.5f);
                        listenBtn.setText("Connecting...");
                        listenBtn.setTextSize(20);
                        tv.setVisibility(View.VISIBLE);//changing firebase database values
                        count = 0;
                        listenTimer.setVisibility(View.VISIBLE);

                        curUserListener = availableListeners.child(Long.toString(System.currentTimeMillis()));
                        curUserListener.setValue(email.substring(0, email.indexOf("@")));

                        //changing boolean value to tell program button is selected
                        availableAsListener = true;
                        editor.putBoolean("listening", true);
                        editor.commit();


                        final DatabaseReference chats = database.getReference("chats");

                        // look through chats for chatID = listenerID
                        chats.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if (!snapshot.getKey().equals("dummy") && snapshot.getKey().equals(email.substring(0, email.indexOf("@")))) {
                                        DatabaseReference chatdb = database.getReference("chats").child(snapshot.getKey());
                                        chatdb.removeValue();

                                        editor.putString("curChat", snapshot.getKey());
                                        editor.putString("name", email);
                                        editor.putBoolean("listening", false);
                                        editor.commit();

                                        chats.removeEventListener(this);
                                        resetListen();
                                        Intent toChat = new Intent(HomeListener.this, Chat.class);
                                        startActivity(toChat);
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    } else {
                        //remove yourself from availableListenersList
                        curUserListener.removeValue();
                        resetListen();
                        availableAsListener = false;
                        editor.putBoolean("listening", false);
                        editor.commit();
                    }
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
        final TextView listenTimer = findViewById(R.id.listenTimer);

        count = 0; //reset waiting time
        //T.cancel(); //stop timer
        listenBtn.setAlpha(1f);
        listenBtn.setText("Listen");
        listenBtn.setTextSize(40);
        tv.setVisibility(View.INVISIBLE);
        listenTimer.setVisibility(View.INVISIBLE);
    }

    public void listenForPushNotifications() {

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
                    if (!settings.getString("email", "email").equals("email")) {

                        //Get my own email so I don't get a notification if I click Listener
                        String myEmail = settings.getString("email", "email").substring(0, email.indexOf("@"));
                        // ignore dummy entry of database
                        if (!snapshot.getKey().equals("dummy") && !snapshot.getValue().equals(myEmail)) {
                            //Should only increment for entries that are not dummy nor myself
                            numAvailableListenerNow++;
                        }
                    }
                }
                // Integer i = new Integer (numAvailableListenerNow);
                // Toast.makeText(getApplicationContext(), i.toString() , Toast.LENGTH_SHORT).show();

                if (noWaitingTalkers && canSendPushNotifs && settings.getBoolean("notify", true)
                        && !settings.getBoolean("isCounselor", true) && numAvailableListenerNow > numAvailableListenersPrev) {
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
        //android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O
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

            //notify.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(0, notify);
        }
        /*
        Intent notificationIntent = new Intent(ctx, YourClass.class);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx,
                YOUR_PI_REQ_CODE, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Resources res = ctx.getResources();
        Notification.Builder builder = new Notification.Builder(ctx);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.some_img)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.some_big_img))
                .setTicker(res.getString(R.string.your_ticker))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(res.getString(R.string.your_notif_title))
                .setContentText(res.getString(R.string.your_notif_text));
        Notification n = builder.build();

        nm.notify(YOUR_NOTIF_ID, n);*/
    }

    public void onHoverButtons() {
        final Button talkBtn = findViewById(R.id.talk);
        final Button listenBtn = findViewById(R.id.listen);

        talkBtn.setOnHoverListener((new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                talkBtn.setAlpha(.2f);
                return true;
            }
        }));

        listenBtn.setOnHoverListener((new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                listenBtn.setAlpha(.2f);
                return true;
            }
        }));
    }


    // if the user is listed as available in Firebase, make them unavailable
    private void makeUnavailable() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Toast.makeText(getApplicationContext(), email, Toast.LENGTH_SHORT).show();

        //removing from availableTalker list if on list.
        final DatabaseReference curTalker = database.getReference("availableTalkers");
        curTalker.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                curTalker.removeEventListener(this);
                DatabaseReference curUser;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getValue().toString().equals(email)) {
                        curUser = curTalker.child(snapshot.getKey());
                        curUser.removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //removing from availableListener list if on list
        final DatabaseReference curListener = database.getReference("availableListeners");
        curListener.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                curListener.removeEventListener(this);
                DatabaseReference curUser;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getValue().toString().equals(email.substring(0, email.indexOf("@")))) {
                        curUser = curListener.child(snapshot.getKey());
                        curUser.removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
