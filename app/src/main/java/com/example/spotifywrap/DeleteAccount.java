
package com.example.spotifywrap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteAccount extends AppCompatActivity {

    private Button resetPasswordButton;
    private TextView back;
    private FirebaseAuth firebaseAuth;
    String username;
    String email;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_account);
        Intent intent = getIntent();
        if(intent != null) {
            username = intent.getStringExtra("username");
            email =  intent.getStringExtra("email");
            token = intent.getStringExtra("token");
        }

        firebaseAuth = FirebaseAuth.getInstance();

        resetPasswordButton = findViewById(R.id.btnReset);
        back = findViewById(R.id.tvLoginHere);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserAccount();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeleteAccount.this, SettingsActivity.class).putExtra("username", username).putExtra("email", email).putExtra("token", token));
            }
        });
    }

    private void deleteUserAccount() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(DeleteAccount.this, "User account has been deleted successfully.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(DeleteAccount.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(DeleteAccount.this, "Failed to delete user account.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            // delete data from firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance(); // db of wrapped for each user
            String UID = user.getUid(); // firebase user id
            db.collection("wraplify").document(UID)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("INFO", "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("ERROR", "Error deleting document", e);
                        }
                    });
        } else {
            Toast.makeText(DeleteAccount.this, "No user logged in.", Toast.LENGTH_SHORT).show();
        }
    }
}



