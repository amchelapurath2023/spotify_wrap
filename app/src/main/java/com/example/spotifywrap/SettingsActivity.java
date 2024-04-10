package com.example.spotifywrap;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseUser;


public class SettingsActivity extends AppCompatActivity {

    TextInputEditText etLoginEmail;
    TextInputEditText etLoginPassword;
    TextView tvRegisterHere;
    Button btnLogin;

    Button reset;
    Button delete;
    Button back;
    Button change;

    String username;
    String email;
    TextView newusername;
    TextView curusername;
    TextView curemail;

    FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("username")) {
            username = intent.getStringExtra("username");


        reset = findViewById(R.id.ResetPw);
        delete = findViewById(R.id.Delete);
        back = findViewById(R.id.backButton);
        change = findViewById(R.id.btnChangeUserName);
        newusername = findViewById(R.id.etResetEmail);
        curemail = findViewById(R.id.email);
        curusername = findViewById(R.id.username);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            email = user.getEmail();
        }

        curusername.setText("Your Account Username: " + username);
        curemail.setText("Your Account Email: " + email);



        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text from the TextView
                username = newusername.getText().toString();

                // Check if the text is not empty
                if (!username.isEmpty()) {
                    // Clear the TextView
                    newusername.setText("");
                    curusername.setText("Your Account Username: " + username);

                    // Now 'variable' contains the text from the TextView
                    // You can use it as needed
                    // For example, display it in a toast message
                    Toast.makeText(SettingsActivity.this, "Username successfully updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, "Enter a new username", Toast.LENGTH_SHORT).show();
                }
            }
        });

        reset.setOnClickListener(view -> {
            startActivity(new Intent(SettingsActivity.this, ResetPassword.class).putExtra("username", username).putExtra("email", email));
        });
        back.setOnClickListener(view -> {
            startActivity(new Intent(SettingsActivity.this, MainActivity.class).putExtra("username", username));
        });
        delete.setOnClickListener(view -> {
            startActivity(new Intent(SettingsActivity.this, DeleteAccount.class).putExtra("username", username).putExtra("email", email));
        });

    }
}}
