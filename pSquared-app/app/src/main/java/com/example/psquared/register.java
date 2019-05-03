package com.example.psquared;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;


public class register extends AppCompatActivity {
    private String email;
    private String password;
    private static Button createb;
    private EditText getEmail;
    private TextView errorMessage;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    //create SharedPreferences variables
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getApplicationContext());

        //initializing variables
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        setContentView(R.layout.activity_register);
        createb = (Button) findViewById(R.id.createButton);
        createb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean pass1 = false;
                boolean pass2 = false;

                errorMessage = (TextView)findViewById(R.id.error);
                getEmail = findViewById(R.id.email);

                // check email correctness
                String input = getEmail.getText().toString();
                if (input.length() > 8) {
                    String end = input.substring(input.length() - 8);
                    if (end.equals("@jhu.edu")) {
                        email = input;
                        pass1 = true;
                    } else {
                        errorMessage.setText("invalid email");
                    }
                } else {
                    errorMessage.setText("invalid email");
                }
                EditText getPassword = findViewById(R.id.password);
                String temp1 = getPassword.getText().toString();
                EditText confirmPassword = findViewById(R.id.confirm);
                String temp2 = confirmPassword.getText().toString();

                // put in requirements for password? length, special char
                if(temp1.length()<6) {
                    errorMessage.setText("password has to be at least 6 characters");
                } else if (temp1.equals(temp2)) {
                    password = temp1;
                    pass2 = true;
                } else {
                    errorMessage.setText("passwords don't match");
                }

                if (pass1 && pass2) {
                    createAccount(email, password);
                }
            }
        });

        //Screen pops off when back button is clicked
        onBack();

    }

    @Override
    protected void onStart() {
        super.onStart();

        // setup shared preferences variables
        settings = getDefaultSharedPreferences(this);
        editor = settings.edit();
    }

    //create the account and add account to database. save username into shared preferences
    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "registration successful", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        // set username value in Database
        DatabaseReference users = database.getReference("Users");
        DatabaseReference thisUser = users.child(email.substring(0, email.indexOf("@")));
        thisUser.setValue(0);

        // bring user back to login screen
        Intent toLogin = new Intent(this, MainActivity.class);
        startActivity(toLogin);
    }

    /**
     * Listener presses the back button.
     */
    public void onBack() {
        final ImageButton backBtn = findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
