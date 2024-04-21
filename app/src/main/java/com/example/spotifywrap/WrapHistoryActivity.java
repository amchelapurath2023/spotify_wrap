package com.example.spotifywrap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
                            Button summaryBtn = new Button(WrapHistoryActivity.this);
                            String wrapDate = document.getId();
                            wrapDate += "\n\n";
//                                summaryBtn.setText(wrapDate);
                            summaryBtn.setText(wrapDate);
                            ArrayList<ArrayList<String>> wrapArtists = (ArrayList<ArrayList<String>>) document.getData().get("topArtists");
                            ArrayList<ArrayList<String>> wrapSongs = (ArrayList<ArrayList<String>>) document.getData().get("topSongs");

                            ArrayList<String> topSongTitles = new ArrayList<String>();
                            for (int j = 0; j < wrapSongs.size(); j++) {
                                topSongTitles.add(wrapSongs.get(j).get(0));
                            }
                            ArrayList<String> topArtistNames = new ArrayList<String>();
                            for (int j = 0; j < wrapArtists.size(); j++) {
                                topArtistNames.add(wrapArtists.get(j).get(0));
                            }

                            Intent intent = new Intent(WrapHistoryActivity.this, playsong.class);
                            intent.putStringArrayListExtra("topSongs", topSongTitles);
                            intent.putStringArrayListExtra("topArtists", topArtistNames);

//                            intent.putStringArrayListExtra("topsongurls", topsongurl);
//                            intent.putStringArrayListExtra("userProfile", userProfileArray);
//                            intent.putExtra("recArtists", recArtists);
//                            intent.putExtra("formatDisplay", formatDisplay.toString());

                            summaryBtn.setOnClickListener(view -> {
                                startActivity(new Intent(WrapHistoryActivity.this, PastWrapActivity.class));
                            });

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
