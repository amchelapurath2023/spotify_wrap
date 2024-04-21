package com.example.spotifywrap;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Arrays;

public class playsong extends AppCompatActivity {
    private static final String TAG = "playsong";

    private TextView displayTextView;
    private MediaPlayer mediaPlayer;
    private Button goBack;
    private int currentTrackIndex = 0;
    ArrayList<String> topsongurls;

    private Map<String, Object> summaries = new HashMap<>(); // local copy of wrapped summaries to upload


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped_final);

        // Get data from intent
        Intent intent = getIntent();
        topsongurls = intent.getStringArrayListExtra("topsongurls");
        ArrayList<ArrayList<String>> recArtists = (ArrayList<ArrayList<String>>) intent.getSerializableExtra("recArtists");
        ArrayList<ArrayList<String>> topArtists = (ArrayList<ArrayList<String>>) intent.getSerializableExtra("topArtists");
        ArrayList<ArrayList<String>> topSongs = (ArrayList<ArrayList<String>>) intent.getSerializableExtra("topSongs");
        ArrayList<String> userProfile = intent.getStringArrayListExtra("userProfile");
        String formatDisplay = intent.getStringExtra("formatDisplay");

        // Initialize views
        TextView reccomended = findViewById(R.id.recArtist);
        goBack = findViewById(R.id.button);
        ImageView topAlbumCoverImageView = findViewById(R.id.top_album_cover);
        TextView topArtistOneTextView = findViewById(R.id.top_artist_one);
        TextView topArtistTwoTextView = findViewById(R.id.top_artist_two);
        TextView topArtistThreeTextView = findViewById(R.id.top_artist_three);
        TextView topArtistFourTextView = findViewById(R.id.top_artist_four);
        TextView topArtistFiveTextView = findViewById(R.id.top_artist_five);
        TextView songOneTextView = findViewById(R.id.top_song_one);
        TextView songTwoTextView = findViewById(R.id.top_song_two);
        TextView songThreeTextView = findViewById(R.id.top_song_three);
        TextView songFourTextView = findViewById(R.id.top_song_four);
        TextView songFiveTextView = findViewById(R.id.top_song_five);




        // Load profile picture into ImageView using Picasso
        String imageUrl = topSongs.get(0).get(2); // Assuming the first element is the image URL

        Picasso.get().load(imageUrl).into(topAlbumCoverImageView, new Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Image loaded successfully");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error loading image: " + e.getMessage());
            }
        });

        // Set click listener for go back button
        goBack.setOnClickListener(view -> {

            // Saving summary to firestore !!!
            FirebaseAuth mAuth;
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            String UID = user.getUid(); // user id
            FirebaseFirestore db = FirebaseFirestore.getInstance(); // db of wrapped for each user
            CollectionReference colRef = db.collection("wraplify").document(UID).collection("wrap-summaries"); // existing collection for a specific user's summaries

            Map<String, Object> docData = new HashMap<>();
            ArrayList<String> topSongTitles = new ArrayList<String>();
            for (int j = 0; j < topSongs.size(); j++) {
                topSongTitles.add(topSongs.get(j).get(0));
            }
            ArrayList<String> topArtistNames = new ArrayList<String>();
            for (int j = 0; j < topArtists.size(); j++) {
                topArtistNames.add(topArtists.get(j).get(0));
            }

            docData.put("topSongs", topSongTitles);
            docData.put("topArtists", topArtistNames);
            SimpleDateFormat sdf = new SimpleDateFormat("'Date\n'dd-MM-yyyy '\n\nand\n\nTime\n'HH:mm:ss z");
            String currentDateAndTime = sdf.format(new Date());
            String summaryId = "summary" + currentDateAndTime;

            Log.d("INFO", "test");
            Log.d("INFO", Integer.toString(topSongs.size()));
            Log.d("INFO", topSongs.get(1).get(1));
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

            releaseMediaPlayer();
            startActivity(new Intent(playsong.this, MainActivity.class));
        });
        reccomended.setText(recArtists.get(0).get(0));


        // Set top artists
        if (topArtists != null && !topArtists.isEmpty()) {
            // Display the top 5 artists, assuming each inner list contains artist names
            ArrayList<TextView> artistTextViews = new ArrayList<>(Arrays.asList(
                    topArtistOneTextView, topArtistTwoTextView, topArtistThreeTextView, topArtistFourTextView, topArtistFiveTextView
            ));
            for (int i = 0; i < Math.min(topArtists.size(), 5); i++) {
                ArrayList<String> artistInfo = topArtists.get(i);
                if (artistInfo.size() >= 1) {
                    int x = i+1;
                    String y = new String(String.valueOf(x));
                    artistTextViews.get(i).setText(y + ". " + artistInfo.get(0)); // Artist name

                }
            }
        }


        // Set top songs
        if (topSongs != null && !topSongs.isEmpty()) {
            // Display the top 5 songs, assuming each inner list contains song information
            ArrayList<TextView> songTextViews = new ArrayList<>(Arrays.asList(
                    songOneTextView, songTwoTextView, songThreeTextView, songFourTextView, songFiveTextView
            ));
            for (int i = 0; i < Math.min(topSongs.size(), 5); i++) {
                ArrayList<String> songInfo = topSongs.get(i);
                if (songInfo.size() >= 1) {
                    int x = i+1;
                    String y = new String(String.valueOf(x));
                    songTextViews.get(i).setText(y + ". " + songInfo.get(0)); // Song name
                }
            }
        }


        // Start playing songs
        playNextTrack();
    }


    // Method to play the next track
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

    // Method to release media player resources
    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        releaseMediaPlayer();
    }
}

