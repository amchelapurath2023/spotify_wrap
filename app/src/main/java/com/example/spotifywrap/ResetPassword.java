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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);
        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
            email =  intent.getStringExtra("email");
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
            startActivity(new Intent(ResetPassword.this, SettingsActivity.class).putExtra("username", username).putExtra("email", email));
        });
    }

    private void resetPassword() {
        String email = emailAddress.getText().toString().trim();

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPassword.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ResetPassword.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

