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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends AppCompatActivity {

    private static Button loginb;
    private static Button registerb;
    private String email;
    private String password;
    private TextView invalidmessage;
    private FirebaseAuth mAuth;

    //create SharedPreferences variables
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set firebase authentication variable
        mAuth = FirebaseAuth.getInstance();

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
        Button dummyBtn = findViewById(R.id.dummy);
        dummyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toHomeDummy = new Intent(MainActivity.this,HomeTalker.class);
                startActivity(toHomeDummy);
            }
        });
    }

    @Override
    // set the SharedPreferences variables
    protected void onStart() {
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
        Toast.makeText(getApplicationContext(), "changing activity", Toast.LENGTH_SHORT).show();
        Intent toHome = new Intent(MainActivity.this, HomeTalker.class);
        startActivity(toHome);
    }

    public void about(View v) {
        Intent toAbout = new Intent(this, About.class);
        startActivity(toAbout);
    }
}
