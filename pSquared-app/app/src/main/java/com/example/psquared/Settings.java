package com.example.psquared;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class Settings extends AppCompatActivity {


    //create SharedPreferences variables
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private String email;
    private String password;
    private FirebaseAuth mAuth;
    EditText emailText;
    EditText pwdText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // setup shared preferences variables
        settings = getDefaultSharedPreferences(this);
        editor = settings.edit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // set up Firebase and UI components
        mAuth = FirebaseAuth.getInstance();

        email = settings.getString("email", "email");
        emailText = findViewById(R.id.email_input);
        emailText.setText(email);

        password = settings.getString("password", "password");
        pwdText = (EditText)findViewById(R.id.password);
        pwdText.setText(password);

    }

    // signs the user out and brings user to login screen
    public void signOut(View v) {
        editor.putString("email", "email");
        editor.putString("password", "password");
        editor.commit();
        mAuth.signOut();
        Intent toLogin = new Intent(this, MainActivity.class);
        startActivity(toLogin);

    }
}
