package com.example.psquared;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends AppCompatActivity {

    private static Button loginb;
    private static Button registerb;
    private String email;
    private String password;
    private TextView invalidmessage;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;

    //create SharedPreferences variables
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toListener();

        //set login buttons
        loginb = (Button) findViewById(R.id.loginButton);
        loginb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get email and password input
                EditText checkEmail = findViewById(R.id.email_login);
                email = checkEmail.getText().toString();
                EditText checkPassword = findViewById(R.id.password_login);
                password = checkPassword.getText().toString();

                // does firebase login.
                if (!(email.equals("") || password.equals(""))) {
                    // Toast.makeText(getApplicationContext(), "not null", Toast.LENGTH_SHORT).show();
                    signInWithEmailAndPassword(email, password);
                } else {
                    invalidmessage = (TextView)findViewById(R.id.invalid);
                    invalidmessage.setText("incorrect email/password combination");
                }
            }
        });
        registerb = (Button) findViewById(R.id.registerButton);
        registerb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,register.class);
                startActivity(intent);
            }
        });

    }

    @Override
    // set the SharedPreferences variables
    protected void onStart() {
        super.onStart();

        // set firebase variables
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        settings = getDefaultSharedPreferences(this);
        editor = settings.edit();

        // automatically direct user to home screen if user is already signed in
        if (!settings.getString("email", "email").equals("email")) {
            checkUserState(settings.getString("email", "email"));
            login();
        }

    }
    // checks Firebase to see if the user is a talker or listener
    private void checkUserState(String email) {
        final DatabaseReference myRef = db.getReference("Users").child(email.substring(0, email.indexOf("@")));
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //getting the listener value from the firebase database
                long value = dataSnapshot.getValue(long.class);
                Integer v = new Integer((int) value);
                // Toast.makeText(getApplicationContext(), v.toString(), Toast.LENGTH_SHORT).show();

                //storing the listener information into the shared preference
                editor.putInt("state", v);
                editor.commit();
                myRef.removeEventListener(this);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Tries to log in user with user inputted email and password
    private void signInWithEmailAndPassword(String email, String password) {
        editor.putString("email", email);
        editor.putString("password", password);
        final String Email = email;
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        invalidmessage = (TextView)findViewById(R.id.invalid);

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            editor.commit();
                            checkUserState(Email);
                            login();
                        } else {
                            // If sign in fails, display a message to the user.
                            invalidmessage.setText("incorrect email/password combination");

                        }

                        // ...
                    }
                });
    }

    //performs necessary actions on successful login
    private void login() {

        //getting the listener value from SharedPreferencee
        int state = settings.getInt("state", 0);
        // Toast.makeText(getApplicationContext(), new Integer(state).toString(), Toast.LENGTH_SHORT).show();

        //navigating to matching home activity
        if (state == 1) {
            Intent toHome = new Intent(MainActivity.this, HomeListener.class);
            startActivity(toHome);
        } else if (state == 2) {
            Intent toHome = new Intent(MainActivity.this, CounselorMain.class);
            startActivity(toHome);
        } else {
            Intent toHome = new Intent(MainActivity.this, HomeTalker.class);
            startActivity(toHome);
        }
    }
    //for testing purposes.
    public void toListener() {
        Intent mainToListener = new Intent(this, HomeListener.class);
        startActivity(mainToListener);
    }
}
