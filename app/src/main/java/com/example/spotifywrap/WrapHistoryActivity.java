package com.example.spotifywrap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class WrapHistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_wraps);

        Button backBtn = (Button) findViewById(R.id.back_button);
        LinearLayout wrapsLayout = (LinearLayout) findViewById(R.id.wraps_linearlayout);

        backBtn.setOnClickListener(view -> {
            startActivity(new Intent(WrapHistoryActivity.this, MainActivity.class));
        });

        // load saved wraps
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String UID = user.getUid(); // firebase user id
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // db of wrapped for each user
        CollectionReference colRef = db.collection("wraplify").document(UID).collection("wrap-summaries"); // existing collection for a specific user's summaries

        colRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Button xmlButton = findViewById(R.id.btnLogin);
                                Button summaryBtn = new Button(WrapHistoryActivity.this);
                                summaryBtn.setLayoutParams(xmlButton.getLayoutParams());
                                summaryBtn.setBackground(xmlButton.getBackground());
//                                summaryBtn.setBackgroundResource(R.drawable.past_wrap_button);
//                                summaryBtn.setBackgroundColor(summaryBtn.getContext().getResources().getColor(R.color.pink));
//                                summaryBtn.set
//                                summaryBtn.setBackground(summaryBtn.getContext().getResources().getAssets(R.id.btnLogin));
//                                summaryBtn.set
                                String wrapDate = document.getId();
//                                summaryBtn.setText(wrapDate);
                                StringBuilder pastWrap = new StringBuilder();
                                pastWrap.append("\n");
                                pastWrap.append(wrapDate);
                                pastWrap.append("\n-----\n\n");
                                pastWrap.append("~TOP ARTISTS~").append("\n\n");
                                ArrayList<String> wrapArtists = (ArrayList<String>) document.getData().get("topArtists");
                                for (int i = 0; i < wrapArtists.size(); i++){
                                    pastWrap.append(wrapArtists.get(i)).append("\n");
                                }
                                pastWrap.append("\n");
                                pastWrap.append("~TOP SONGS~").append("\n\n");
                                ArrayList<String> wrapSongs = (ArrayList<String>) document.getData().get("topSongs");
                                for (int i = 0; i < wrapSongs.size(); i++){
                                    pastWrap.append(wrapSongs.get(i)).append("\n");
                                }
                                summaryBtn.setText(pastWrap);

                                summaryBtn.setTextColor(summaryBtn.getContext().getResources().getColor(R.color.white));
//                                summaryBtn.setTextAppearance(android.R.style);
                                summaryBtn.setTextSize(20);
                                summaryBtn.setTypeface(getResources().getFont(R.font.urbanist_regular));
                                summaryBtn.setGravity(Gravity.CENTER);


                                wrapsLayout.addView(summaryBtn);
                                Log.d("INFO", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("ERROR", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user == null){
//            startActivity(new Intent(MainActivity.this, LoginActivity.class));
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
