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

import com.example.spotifywrap.playsong;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.ArrayList;

public class Songs extends AppCompatActivity {

    private TextView displayTextView;
    private MediaPlayer mediaPlayer;
    private Button goBack;
    private int currentTrackIndex = 0;
    ArrayList<String> topsongurls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        // Get data from intent
        Intent intent = getIntent();
        topsongurls = intent.getStringArrayListExtra("topsongurls");
        ArrayList<ArrayList<String>> topSongs = (ArrayList<ArrayList<String>>) intent.getSerializableExtra("topSongs");

        // Initialize views
        TextView textViewTopSongs = findViewById(R.id.textViewTopSongs);
        ImageView[] songImageViews = {
                findViewById(R.id.imageViewSong1),
                findViewById(R.id.imageViewSong2),
                findViewById(R.id.imageViewSong3),
                findViewById(R.id.imageViewSong4),
                findViewById(R.id.imageViewSong5)
        };
        TextView[] songNameTextViews = {
                findViewById(R.id.textViewSong1),
                findViewById(R.id.textViewSong2),
                findViewById(R.id.textViewSong3),
                findViewById(R.id.textViewSong4),
                findViewById(R.id.textViewSong5)
        };
        TextView[] artistNameTextViews = {
                findViewById(R.id.textViewArtist1),
                findViewById(R.id.textViewArtist2),
                findViewById(R.id.textViewArtist3),
                findViewById(R.id.textViewArtist4),
                findViewById(R.id.textViewArtist5)
        };
        goBack = findViewById(R.id.button);

        // Set top songs
        if (topsongurls != null && !topsongurls.isEmpty()) {
            for (int i = 0; i < Math.min(topsongurls.size(), 5); i++) {
                String songUrl = topsongurls.get(i);
                String songName = topSongs.get(i).get(0);
                String artistName = topSongs.get(i).get(1);

                // Set song name and artist name
                songNameTextViews[i].setText(songName);
                artistNameTextViews[i].setText(artistName);

                // Load image using Picasso
                // Note: Assuming the image URL is stored in the third position of each song data ArrayList
                String imageUrl = topSongs.get(i).get(2);
                Picasso.get().load(imageUrl).into(songImageViews[i], new Callback() {
                    @Override
                    public void onSuccess() {
                        // Image loaded successfully
                    }

                    @Override
                    public void onError(Exception e) {
                        // Error loading image
                        Log.e("Picasso", "Error loading image: " + e.getMessage());
                    }
                });
            }
        }

        // Set click listener for go back button
        goBack.setOnClickListener(view -> {
            releaseMediaPlayer();
            startActivity(new Intent(Songs.this, MainActivity.class));
        });

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
        releaseMediaPlayer();
    }
}
