package com.example.psquared;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static Button loginb;
    private static Button registerb;
    private String email;
    private String password;
    private TextView invalidmessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginb = (Button) findViewById(R.id.loginButton);
        loginb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText checkEmail = findViewById(R.id.email_login);
                email = checkEmail.getText().toString();
                EditText checkPassword = findViewById(R.id.password_login);
                password = checkPassword.getText().toString();
                // send email and password pair to database to check if present
                // if no such email and password
                invalidmessage = (TextView)findViewById(R.id.invalid);
                invalidmessage.setText("incorrect email/password combination");
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
}
