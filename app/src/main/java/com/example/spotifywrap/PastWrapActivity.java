package com.example.spotifywrap;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class PastWrapActivity extends AppCompatActivity {

    private static final String TAG = "playsong";

    private TextView displayTextView;
    private MediaPlayer mediaPlayer;
    private Button goBack;
    private int currentTrackIndex = 0;
    ArrayList<String> topsongurls;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_wrap);

        // Get data from intent
        Intent intent = getIntent();
//        topsongurls = intent.getStringArrayListExtra("topsongurls");
//        ArrayList<ArrayList<String>> recArtists = (ArrayList<ArrayList<String>>) intent.getSerializableExtra("recArtists");
        ArrayList<ArrayList<String>> topArtists = (ArrayList<ArrayList<String>>) intent.getSerializableExtra("topArtists");
        ArrayList<ArrayList<String>> topSongs = (ArrayList<ArrayList<String>>) intent.getSerializableExtra("topSongs");
//        ArrayList<String> userProfile = intent.getStringArrayListExtra("userProfile");
//        String formatDisplay = intent.getStringExtra("formatDisplay");

        // Initialize views
        TextView reccomended = findViewById(R.id.recArtist);
        goBack = findViewById(R.id.backButton);
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



        // Set click listener for go back button
        goBack.setOnClickListener(view -> {
//            releaseMediaPlayer();
            startActivity(new Intent(PastWrapActivity.this, WrapHistoryActivity.class));
        });

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


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
