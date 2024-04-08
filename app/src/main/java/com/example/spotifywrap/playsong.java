package com.example.spotifywrap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class playsong extends AppCompatActivity {
    // class to handle wrapped activity, where the spotify wrapped is displayed
    private TextView displayTextView;
    private MediaPlayer mediaPlayer;
    private int currentTrackIndex = 0;
    ArrayList<String> topsongurls;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        // this portion contains the data from the api cals
        Intent intent = getIntent();

        topsongurls = intent.getStringArrayListExtra("topsongurls");
        ArrayList<ArrayList<String>> recArtists = (ArrayList<ArrayList<String>>) getIntent().getSerializableExtra("recArtists");
        ArrayList<ArrayList<String>> topArtists = (ArrayList<ArrayList<String>>) getIntent().getSerializableExtra("topArtists");
        ArrayList<ArrayList<String>> topSongs = (ArrayList<ArrayList<String>>) getIntent().getSerializableExtra("topSongs");
        ArrayList<String> userProfile = intent.getStringArrayListExtra("userProfile");
        String formatUser = getIntent().getStringExtra("formatUser");
        String formatArtist = getIntent().getStringExtra("formatArtist");
        String formatSong = getIntent().getStringExtra("formatSong");
        String formatRec = getIntent().getStringExtra("formatUser");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playsong);
        // ---------------------------------

        displayTextView = findViewById(R.id.display_text);

        StringBuilder dummy = new StringBuilder();
//        for (int i = 0; i < userProfile.size(); i++){
//            dummy.append(userProfile.get(i));
//        }
//        for (int i = 0; i < topArtists.size(); i++){
//            for (int j = 0; j < 3; j++) {
//                dummy.append(topArtists.get(i).get(j));
//            }
//        }
//        for (int i = 0; i < recArtists.size(); i++){
//            for (int j = 0; j < 3; j++) {
//                dummy.append(recArtists.get(i).get(j));
//            }
//        }
//        for (int i = 0; i < topSongs.size(); i++){
//            for (int j = 0; j < 4; j++) {
//                dummy.append(topSongs.get(i).get(j));
//            }
//        }
        dummy.append(formatUser).append(formatArtist).append(formatSong).append(formatRec);
        setTextAsync(dummy.toString(), displayTextView);
        playNextTrack();
    }
    //method to play all top tracks preview
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
                Toast.makeText(this, "Audio started playing..", Toast.LENGTH_SHORT).show();

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
            Toast.makeText(this, "No more tracks to play", Toast.LENGTH_SHORT).show();
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