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

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class playsong extends AppCompatActivity {
    private static final String TAG = "playsong";

    private TextView displayTextView;
    private MediaPlayer mediaPlayer;
    private Button goBack;
    private int currentTrackIndex = 0;
    ArrayList<String> topsongurls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped_final);

        // Get data from intent
        Intent intent = getIntent();
        topsongurls = intent.getStringArrayListExtra("topsongurls");
        ArrayList<ArrayList<String>> recArtists = (ArrayList<ArrayList<String>>) intent.getSerializableExtra("recArtists");
        ArrayList<ArrayList<String>> topArtists = (ArrayList<ArrayList<String>>) intent.getSerializableExtra("topArtistsNames");
        ArrayList<ArrayList<String>> topSongs = (ArrayList<ArrayList<String>>) intent.getSerializableExtra("topSongs");


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


    }


    // Method to play the next track
    private void playNextTrack() {
        if (currentTrackIndex < topsongurls.size()) {
            String audioUrl = topsongurls.get(currentTrackIndex);

            try {
                mediaPlayer.reset(); // Reset the MediaPlayer to play the next track
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
        releaseMediaPlayer();
    }
}

