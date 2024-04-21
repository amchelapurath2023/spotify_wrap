package com.example.spotifywrap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private EditText emailAddress;
    private Button resetPasswordButton;
    private TextView back;
    String username;
    String email;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);
        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
            email =  intent.getStringExtra("email");
            token = intent.getStringExtra("token");
        }


        emailAddress = findViewById(R.id.etResetEmail);
        resetPasswordButton = findViewById(R.id.btnReset);
        back = findViewById(R.id.tvLoginHere);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
        back.setOnClickListener(view ->{
            startActivity(new Intent(ResetPassword.this, SettingsActivity.class).putExtra("username", username).putExtra("email", email).putExtra("token", token));
        });
    }

    private void resetPassword() {
        String email = emailAddress.getText().toString().trim();
        if (email.isEmpty()){
            emailAddress.setError("Enter an email");
            return;
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPassword.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ResetPassword.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                            emailAddress.setError("Invalid email");
                        }
                    }
                });
    }
}

