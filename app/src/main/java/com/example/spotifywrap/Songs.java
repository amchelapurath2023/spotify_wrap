package com.example.spotifywrap;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.spotifywrap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import androidx.annotation.NonNull;


public class Songs extends AppCompatActivity implements TopSongsFragment.OnGoToTopArtistsListener, TopArtistFragment.OnGoToFinalWrapListener  {

    private MediaPlayer mediaPlayer;
    private ArrayList<String> topsongurls;
    private ArrayList<ArrayList<String>> topArtists;
    private ArrayList<ArrayList<String>> recArtists;
    private ArrayList<ArrayList<String>> topSongs;
    private int currentTrackIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        // Get data from intent
        Intent intent = getIntent();
        topsongurls = intent.getStringArrayListExtra("topsongurls");
        topSongs = (ArrayList<ArrayList<String>>) intent.getSerializableExtra("topSongs");
        topArtists = (ArrayList<ArrayList<String>>) intent.getSerializableExtra("topArtists");
        recArtists = (ArrayList<ArrayList<String>>) intent.getSerializableExtra("recArtists");


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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'\n'HH:mm:ss z");
        String currentDateAndTime = sdf.format(new Date());
        String summaryId = currentDateAndTime;

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



        // Initialize media player
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);



        // Load first track
        playNextTrack();

        // Display top songs fragment initially
        displayTopSongsFragment();


        // Set click listener for go back button
        Button goBack = findViewById(R.id.button);
        goBack.setOnClickListener(view -> onBackPressed());
    }

    private void displayTopSongsFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        TopSongsFragment fragment = new TopSongsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("topsongurls", topsongurls);
        args.putSerializable("topArtists", topArtists);
        args.putSerializable("topSongs", topSongs);
        fragment.setArguments(args);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void displayTopArtistsFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        TopArtistFragment fragment = new TopArtistFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("topsongurls", topsongurls);
        args.putSerializable("topArtists", topArtists);
        fragment.setArguments(args);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void displayFinalWrapFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FinalWrapFragment fragment = new FinalWrapFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("topsongurls", topsongurls);
        args.putSerializable("topArtists", topArtists);
        args.putSerializable("topSongs", topSongs);
        args.putSerializable("recArtists", recArtists);
        fragment.setArguments(args);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void playNextTrack() {
        if (currentTrackIndex < topsongurls.size()) {
            String audioUrl = topsongurls.get(currentTrackIndex);
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(audioUrl);
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> {
                    currentTrackIndex++;
                    playNextTrack();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No more tracks to play", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onGoToTopArtists() {
        displayTopArtistsFragment();
    }
    @Override
    public void onGoToFinalWrap() {
        displayFinalWrapFragment();
    }
}
