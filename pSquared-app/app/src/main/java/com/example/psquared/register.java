package com.example.psquared;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class register extends AppCompatActivity {
    private String email;
    private String password;
    private static Button createb;
    private EditText getEmail;
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        createb = (Button) findViewById(R.id.createButton);
        createb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorMessage = (TextView)findViewById(R.id.error);
                EditText getEmail = findViewById(R.id.email);
                // check email correctness
                String input = getEmail.getText().toString();
                if (input.length() > 8) {
                    String end = input.substring(input.length() - 8);
                    if (end.equals("@jhu.edu")) {
                        email = input;
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
                if (temp1.equals(temp2)) {
                    password = temp1;
                } else {
                    errorMessage.setText("passwords don't match");
                }
            }
        });


    }
}
