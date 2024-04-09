package com.example.spotifywrap;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;


public class SettingsActivity extends AppCompatActivity {

    TextInputEditText etLoginEmail;
    TextInputEditText etLoginPassword;
    TextView tvRegisterHere;
    Button btnLogin;

    Button reset;
    Button delete;
    Button back;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        reset = findViewById(R.id.ResetPw);
        delete = findViewById(R.id.Delete);
        back = findViewById(R.id.backButton);




        reset.setOnClickListener(view -> {
            startActivity(new Intent(SettingsActivity.this, ResetPassword.class));
        });
        back.setOnClickListener(view -> {
            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
        });
        delete.setOnClickListener(view -> {
            startActivity(new Intent(SettingsActivity.this, DeleteAccount.class));
        });

    }
}
