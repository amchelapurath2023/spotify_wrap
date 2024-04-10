package com.example.spotifywrap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class playsong extends AppCompatActivity {
    // class to handle wrapped activity, where the spotify wrapped is displayed
    private TextView displayTextView;
    private MediaPlayer mediaPlayer;
    private int currentTrackIndex = 0;
    ArrayList<String> topsongurls;

    private Map<String, Object> summaries = new HashMap<>(); // local copy of wrapped summaries to upload


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        //!!!!!!!!!!!!!!!!!!!!!
        // this portion contains the data from the api calls
        // Top song urls is an array containing the urls of the top songs for song playback
        // userProfile is an arraylist containing the user info in an array form.
        // topArtists, topSongs, recArtists are arraylists of arraylists.
        // Basically, each nested arraylist is an artist/song.
        // The proper indices to access whatever things u need inside the arrays can be found on our meeting docs under the notes for 4/7/24.
        //The formatDisplay is a placeholder display so that the info from userProfile, topArtists, topSongs, recArtists is readable.
        Intent intent = getIntent();

        topsongurls = intent.getStringArrayListExtra("topsongurls");
        ArrayList<ArrayList<String>> recArtists = (ArrayList<ArrayList<String>>) getIntent().getSerializableExtra("recArtists");
        ArrayList<ArrayList<String>> topArtists = (ArrayList<ArrayList<String>>) getIntent().getSerializableExtra("topArtists");
        ArrayList<ArrayList<String>> topSongs = (ArrayList<ArrayList<String>>) getIntent().getSerializableExtra("topSongs");
        ArrayList<String> userProfile = intent.getStringArrayListExtra("userProfile");
        String formatDisplay = getIntent().getStringExtra("formatDisplay");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.playsong);
        // ---------------------------------
        // here I am first just displaying the formatted display string just so there is some summary. You can change this part for UI

        displayTextView = findViewById(R.id.display_text);

        StringBuilder dummy = new StringBuilder();
        dummy.append(formatDisplay);
        setTextAsync(dummy.toString(), displayTextView);
        playNextTrack(); // you need to keep this cus it calls the song playback method.
        Toast.makeText(this, "Playing your top songs!", Toast.LENGTH_SHORT).show();// keep this too


        // Saving summary to firestore !!!
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String UID = user.getUid(); // user id
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // db of wrapped for each user
        CollectionReference colRef = db.collection("wraplify").document(UID).collection("wrap-summaries"); // existing collection for a specific user's summaries

        Map<String, Object> docData = new HashMap<>();
        ArrayList<String> topSongTitles = new ArrayList<String>();
        ArrayList<String> topArtistNames = new ArrayList<String>();
        docData.put("topSongs", topSongTitles);
        docData.put("topArtists", topArtistNames);

        SimpleDateFormat sdf = new SimpleDateFormat("'Date\n'dd-MM-yyyy '\n\nand\n\nTime\n'HH:mm:ss z");
        String currentDateAndTime = sdf.format(new Date());
        String summaryId = "summary" + currentDateAndTime;

        colRef.document(summaryId)
                .set(docData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("INFO", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ERROR", "Error writing document", e);
                    }
                });





    }


    //method to play all top tracks preview. Dont change this part plsss
    private void playNextTrack() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (currentTrackIndex < topsongurls.size()) {
            String audioUrl = topsongurls.get(currentTrackIndex);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            try {
                mediaPlayer.setDataSource(audioUrl);
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> {
                    // When current track finishes playing, play the next track
                    currentTrackIndex++;
                    playNextTrack();
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // No more tracks to play
            Toast.makeText(this, "Hope You Enjoyed Your Summary!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}