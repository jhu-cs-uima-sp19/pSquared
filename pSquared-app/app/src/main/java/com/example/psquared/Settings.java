package com.example.psquared;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class Settings extends AppCompatActivity {

    private Context mContext;
    private Activity mActivity;
    private ConstraintLayout mLayout;
    private PopupWindow mPopupWindow;

    //create SharedPreferences variables
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private String email;
    private FirebaseAuth mAuth;
    private TextView emailText;
    private TextView change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        mContext = getApplicationContext();
        mActivity = Settings.this;
        mLayout = (ConstraintLayout) findViewById(R.id.settings_layout);

        //Screen pops off when back button is clicked
        onTalk();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // set up Firebase and UI components
        mAuth = FirebaseAuth.getInstance();

        email = settings.getString("email", "email");
        emailText = findViewById(R.id.email_text);
        emailText.setText(email);

        change = (TextView) findViewById(R.id.changepassword);
        change.setText("Change password");
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.popup,null);
                mPopupWindow = new PopupWindow(
                        customView,
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );

                if(Build.VERSION.SDK_INT>=21){
                    mPopupWindow.setElevation(5.0f);
                }
                ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Dismiss the popup window
                        mPopupWindow.dismiss();
                    }
                });

                Button save = customView.findViewById(R.id.save);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Dismiss the popup window
                        mPopupWindow.dismiss();
                    }
                });

                /*
                EditText oldpassword = customView.findViewById(R.id.oldpwd);
                EditText newpassword = customView.findViewById(R.id.newpwd);
                EditText confirmpasword = customView.findViewById(R.id.confirm);
                String old = oldpassword.getText().toString();
                String pwd = newpassword.getText().toString();
                String confirm = confirmpasword.getText().toString();
*/

                mPopupWindow.showAtLocation(mLayout, Gravity.CENTER,0,0);
            }

        });
    }



    /**
     * Listener presses the back button.
     */
    public void onTalk() {
        final ImageButton backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // setup shared preferences variables
        settings = getDefaultSharedPreferences(this);
        editor = settings.edit();
    }

    // signs the user out and brings user to login screen
    public void signOut(View v) {
        mAuth.signOut();

        makeUnavailable();

        editor.putString("email", "email");
        editor.putString("password", "password");
        editor.commit();

        Intent toLogin = new Intent(this, MainActivity.class);
        startActivity(toLogin);

    }

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
