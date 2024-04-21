package com.example.spotifywrap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Arrays;

public class FinalWrapFragment extends Fragment {

    private ArrayList<String> topsongurls;
    private ArrayList<ArrayList<String>> recArtists;
    private ArrayList<ArrayList<String>> topArtists;
    private ArrayList<ArrayList<String>> topSongs;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_final_wrap, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Get data from arguments
        Bundle args = getArguments();
        if (args != null) {
            topsongurls = args.getStringArrayList("topsongurls");
            recArtists = (ArrayList<ArrayList<String>>) args.getSerializable("recArtists");
            topArtists = (ArrayList<ArrayList<String>>) args.getSerializable("topArtists");
            topSongs = (ArrayList<ArrayList<String>>) args.getSerializable("topSongs");
        }

        // Initialize views
        TextView reccomended = view.findViewById(R.id.recArtist);
        Button goBack = view.findViewById(R.id.button);
        ImageView topAlbumCoverImageView = view.findViewById(R.id.top_album_cover);
        TextView topArtistOneTextView = view.findViewById(R.id.top_artist_one);
        TextView topArtistTwoTextView = view.findViewById(R.id.top_artist_two);
        TextView topArtistThreeTextView = view.findViewById(R.id.top_artist_three);
        TextView topArtistFourTextView = view.findViewById(R.id.top_artist_four);
        TextView topArtistFiveTextView = view.findViewById(R.id.top_artist_five);
        TextView songOneTextView = view.findViewById(R.id.top_song_one);
        TextView songTwoTextView = view.findViewById(R.id.top_song_two);
        TextView songThreeTextView = view.findViewById(R.id.top_song_three);
        TextView songFourTextView = view.findViewById(R.id.top_song_four);
        TextView songFiveTextView = view.findViewById(R.id.top_song_five);

        // Load profile picture into ImageView using Picasso
        if (topSongs != null && !topSongs.isEmpty()) {
            String imageUrl = topSongs.get(0).get(2); // Assuming the first element is the image URL
            Picasso.get().load(imageUrl).into(topAlbumCoverImageView, new Callback() {
                @Override
                public void onSuccess() {
                    // Image loaded successfully
                }

                @Override
                public void onError(Exception e) {
                    // Error loading image
                }
            });
        }

        // Set recommended artist
        if (recArtists != null && !recArtists.isEmpty()) {
            reccomended.setText(recArtists.get(0).get(0));
        }

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
                    System.out.println(artistInfo.get(0));
                    artistTextViews.get(i).setText(y + ". " + artistInfo.get(0)); // Artist name

                }
            }
        }

        // Set top songs
        if (topSongs != null && !topSongs.isEmpty()) {
            ArrayList<TextView> songTextViews = new ArrayList<>(Arrays.asList(
                    songOneTextView, songTwoTextView, songThreeTextView, songFourTextView, songFiveTextView
            ));
            for (int i = 0; i < Math.min(topSongs.size(), 5); i++) {
                ArrayList<String> songInfo = topSongs.get(i);
                if (songInfo.size() >= 1) {
                    int x = i + 1;
                    songTextViews.get(i).setText(x + ". " + songInfo.get(0)); // Song name
                }
            }
        }

        // Set click listener for go back button
        goBack.setOnClickListener(v -> {
            // Stop music playback
            stopMusic();

            // Navigate back to MainActivity
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);

            // Finish the current activity
            getActivity().finish();
        });

    }
    private void stopMusic() {
        // Access the Songs activity and stop the music playback
        if (getActivity() instanceof Songs) {
            Songs songsActivity = (Songs) getActivity();
            songsActivity.stopMusic();
        }
    }
}
