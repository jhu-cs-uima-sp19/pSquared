package com.example.psquared;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class register extends AppCompatActivity {
    private String email;
    private String password;
    private static Button createb;
    private EditText getemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        createb = (Button) findViewById(R.id.createButton);
        createb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText getemail = findViewById(R.id.email);
                // check email correctness
                email = getemail.getText().toString();
                EditText getPassword = findViewById(R.id.password);
                String temp1 = getPassword.getText().toString();
                EditText confirmPassword = findViewById(R.id.confirm);
                String temp2 = confirmPassword.getText().toString();
                // put in requirements for password? length, special char
                if (temp1.equals(temp2)) {
                    password = temp1;
                }
            }
        });


    }
}
