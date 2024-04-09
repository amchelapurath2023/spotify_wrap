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