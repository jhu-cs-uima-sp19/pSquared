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
import android.widget.CompoundButton;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
    private Button change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        mContext = getApplicationContext();
        mActivity = Settings.this;
        mLayout = (ConstraintLayout) findViewById(R.id.settings_layout);

        settings = getDefaultSharedPreferences(this);
        editor = settings.edit();
        //Screen pops off when back button is clicked
        onTalk();

        configureNotify();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // set up Firebase and UI components
        mAuth = FirebaseAuth.getInstance();

        email = settings.getString("email", "email");
        emailText = findViewById(R.id.email_text);
        emailText.setText(email);

        //Function to change password
        changePwd();

        //password = "password";
        //password = settings.getString("password", "password");
        // EditText pwdText = findViewById(R.id.password);
        // pwdText.setText(password);
    }


    public void changePwd() {
        change = findViewById(R.id.changePassword);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                final View customView = inflater.inflate(R.layout.popup,null);

                mPopupWindow = new PopupWindow(
                        customView,
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );

                mPopupWindow.setFocusable(true);

                if(Build.VERSION.SDK_INT>=21){
                    mPopupWindow.setElevation(5.0f);
                }

                //Exit without changing anything if user presses Cancel
                Button cancelButton = customView.findViewById(R.id.cancel);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Dismiss the popup window
                        mPopupWindow.dismiss();
                    }
                });

                //Exit with saved changes if user presses Save
                Button save = customView.findViewById(R.id.save);
                save.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        //Find all user inputs in edit texts
                        EditText oldPassword = customView.findViewById(R.id.oldPwd);
                        EditText newPassword = customView.findViewById(R.id.newPwd);
                        EditText confirmPassword = customView.findViewById(R.id.confirmPwd);
                        String oldPwd = oldPassword.getText().toString();
                        String newPwd = newPassword.getText().toString();
                        String confirmPwd = confirmPassword.getText().toString();

                        if (oldPwd.equals("") || newPwd.equals("") || confirmPwd.equals("")) {
                            Toast.makeText(getApplicationContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                        } else if (!oldPwd.equals(settings.getString("password", ""))) {
                            Toast.makeText(getApplicationContext(), "Incorrect current password", Toast.LENGTH_SHORT).show();
                        } else if (!newPwd.equals(confirmPwd)) {
                            Toast.makeText(getApplicationContext(), "New Password does not match the Confirm Password", Toast.LENGTH_SHORT).show();
                        } else { //everything is successful

                            /*
                            //Update in Shared Preferences
                            editor.putString("password", newPwd);
                            editor.commit();

                            //Re-authenticate user in Firebase
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String myEmail = settings.getString("email", "");
                            String myPwd = settings.getString("password", "");
                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(myEmail, myPwd);
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "yes", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "no", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            //Update in Firebase
                            //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.updatePassword(newPwd)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Password successfully updated!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Something went wrong.. password unchanged", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            // Dismiss the popup window
                            mPopupWindow.dismiss();
                            */
                        }
                    }
                });

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
        toLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(toLogin);

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

    // apply and save notification settings.
    private void configureNotify() {
        Switch not = findViewById(R.id.not1);
        not.setChecked(settings.getBoolean("notify", true));
        not.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("notify", isChecked);
                editor.commit();

            }
        });
    }

}
