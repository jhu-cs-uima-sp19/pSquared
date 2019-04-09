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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        //set login buttons
        loginb = (Button) findViewById(R.id.loginButton);
        loginb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText checkEmail = findViewById(R.id.email_login);
                email = checkEmail.getText().toString();
                EditText checkPassword = findViewById(R.id.password_login);
                password = checkPassword.getText().toString();

                // does firebase login.
                signInWithEmailAndPassword(email, password);
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

        //set firebase variables
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        super.onStart();
        settings = getDefaultSharedPreferences(this);
        editor = settings.edit();
    }

    //Tries to log in user with email and password
    private void signInWithEmailAndPassword(String email, String password) {
        editor.putString("email", email);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        invalidmessage = (TextView)findViewById(R.id.invalid);

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            editor.commit();
                            FirebaseUser user = mAuth.getCurrentUser();
                            login(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            invalidmessage.setText("incorrect email/password combination");

                        }

                        // ...
                    }
                });
    }

    //performs necessary actions on successful login
    private void login(FirebaseUser user) {
        String email = user.getEmail();
        DatabaseReference myRef = db.getReference("Users").child(email.substring(0, email.indexOf("@")));
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //getting the listener value from the firebase database
                long value = dataSnapshot.getValue(long.class);

                //storing the listener information into the shared preferences
                if (value == 1) {
                    editor.putBoolean("Listener", true);
                } else {
                    editor.putBoolean("Listener", false);
                }
                editor.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //getting the listener value from SharedPreferences
        boolean listener = settings.getBoolean("Listener", false);

        //navigating to matching home activity
        if (listener) {
            Intent toHome = new Intent(MainActivity.this, HomeListener.class);
            startActivity(toHome);
        } else {

            Intent toHome = new Intent(MainActivity.this, HomeTalker.class);
            startActivity(toHome);
        }
    }

    public void about(View v) {
        Intent toAbout = new Intent(this, About.class);
        startActivity(toAbout);
    }
}
