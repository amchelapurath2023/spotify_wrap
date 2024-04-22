package com.example.spotifywrap;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class TopSongsFragment extends Fragment {

    private ArrayList<String> topsongurls;
    private ArrayList<ArrayList<String>> topArtists;
    private int currentTrackIndex = 0;
    private ArrayList<ArrayList<String>> topSongs;
    private Button goBack;

    private String username;

    private OnGoToTopArtistsListener goToTopArtistsListener;

    public interface OnGoToTopArtistsListener {
        void onGoToTopArtists();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            goToTopArtistsListener = (OnGoToTopArtistsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnGoToTopArtistsListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_top_songs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button goToTopArtistsButton = view.findViewById(R.id.go_to_top_artists_button);
        goToTopArtistsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goToTopArtistsListener != null) {
                    goToTopArtistsListener.onGoToTopArtists();
                }
            }
        });

        // Get data from arguments
        Bundle args = getArguments();
        if (args != null) {
            topsongurls = args.getStringArrayList("topsongurls");
            topSongs = (ArrayList<ArrayList<String>>) args.getSerializable("topSongs");
            topArtists = (ArrayList<ArrayList<String>>) args.getSerializable("topArtists");
        }

        // Initialize views
        ImageView[] songImageViews = {
                view.findViewById(R.id.imageViewSong1),
                view.findViewById(R.id.imageViewSong2),
                view.findViewById(R.id.imageViewSong3),
                view.findViewById(R.id.imageViewSong4),
                view.findViewById(R.id.imageViewSong5)
        };
        TextView[] songNameTextViews = {
                view.findViewById(R.id.textViewSong1),
                view.findViewById(R.id.textViewSong2),
                view.findViewById(R.id.textViewSong3),
                view.findViewById(R.id.textViewSong4),
                view.findViewById(R.id.textViewSong5)
        };

        TextView[] artistNameTextViews = {
                view.findViewById(R.id.textViewArtist1),
                view.findViewById(R.id.textViewArtist2),
                view.findViewById(R.id.textViewArtist3),
                view.findViewById(R.id.textViewArtist4),
                view.findViewById(R.id.textViewArtist5)
        };




        TextView topA = view.findViewById(R.id.textViewTopSongs);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance(); // db of wrapped for each user
        if (user != null) {
            String UID = user.getUid(); // firebase user id
            DocumentReference docRef = db.collection("wraplify").document(UID);
            Source source = Source.CACHE;

            // Get the document, forcing the SDK to use the offline cache
            docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        // Document found in the offline cache
                        DocumentSnapshot document = task.getResult();
                        username = document.getData().get("username").toString();
                        topA.setText(username + "'s Top Artists");
                        Log.d("INFO", "Cached document data: " + document.getData());
                    } else {
                        Log.d("ERROR", "Cached get failed: ", task.getException());
                    }
                }
            });
        }
        else {
            Log.d("ERROR", "not logged in");

        }

        goBack = view.findViewById(R.id.button);
        // Populate UI with top song data
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

    }
}
