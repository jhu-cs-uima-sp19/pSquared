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
            login(settings.getString("email", "email"));
            //login();
        }

    }
    // checks Firebase to see if the user is a talker or listener
    private void login(String email) {
        final DatabaseReference myRef = db.getReference("Users").child(email.substring(0, email.indexOf("@")));

        // create intents to redirect to home screens
        final Intent toCounserlor = new Intent(MainActivity.this, CounselorMain.class);
        final Intent toTalker = new Intent(MainActivity.this, HomeTalker.class);
        final Intent toListener = new Intent(MainActivity.this, HomeListener.class);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //getting the listener value from the firebase database
                long value = dataSnapshot.getValue(long.class);
                Integer v = new Integer((int) value);
                Toast.makeText(getApplicationContext(), v.toString(), Toast.LENGTH_SHORT).show();

                //storing the listener information into the shared preference
                myRef.removeEventListener(this);
                switch (v) {
                    case 0:
                        Toast.makeText(getApplicationContext(), v.toString(), Toast.LENGTH_SHORT).show();
                        startActivity(toTalker);
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), v.toString(), Toast.LENGTH_SHORT).show();
                        startActivity(toListener);
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), v.toString(), Toast.LENGTH_SHORT).show();
                        startActivity(toCounserlor);
                        break;
                    default:
                        startActivity(toTalker);

                }
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
                            login(Email);
                        } else {
                            // If sign in fails, display a message to the user.
                            invalidmessage.setText("incorrect email/password combination");

                        }

                        // ...
                    }
                });
    }
}
