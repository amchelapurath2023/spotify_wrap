package com.example.spotifywrap;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;
import android.media.AudioManager;
import android.media.MediaPlayer;
public class MainActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "8852c891d90a44e8be613f09ded6d8b3";
    public static final String REDIRECT_URI = "spotifywrap://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode;
    private Call mCall;

    private TextView tokenTextView, codeTextView, profileTextView, artistTextView, trackTextView,relatedTextView;
    private String recArtist;
    private ArrayList<String> favArtists = new ArrayList<>();
    private String var = "short";
    private ArrayList<String> topsongurl = new ArrayList<>();



    private ArrayList<String> userProfileArray = new ArrayList<>();
    private ArrayList<ArrayList<String>> topArtists = new ArrayList<>();

    private ArrayList<ArrayList<String>> topSongs = new ArrayList<>();

    private ArrayList<ArrayList<String>> recArtists = new ArrayList<>();

    MediaPlayer mediaPlayer;




    Button btnLogOut;
    FirebaseAuth mAuth;


    // I have commented out the textviews for the token and code, as well as the get code button functionality and method.
    // You can uncomment for debugging purposes.
    //Make sure to sync with activity_main.xml
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the views
//        tokenTextView = (TextView) findViewById(R.id.token_text_view);
//        codeTextView = (TextView) findViewById(R.id.code_text_view);
        profileTextView = (TextView) findViewById(R.id.profile_text_view);
        artistTextView = (TextView) findViewById(R.id.artist_text_view);
        trackTextView = (TextView) findViewById(R.id.track_text_view);
        relatedTextView = (TextView) findViewById(R.id.related_text_view);


        // Initialize the buttons
        Button tokenBtn = (Button) findViewById(R.id.connect_btn);
//        Button codeBtn = (Button) findViewById(R.id.code_btn);
        Button shortBtn = (Button) findViewById(R.id.timeframe_button_short);
        Button mediumBtn = (Button) findViewById(R.id.timeframe_button_medium);
        Button longBtn = (Button) findViewById(R.id.timeframe_button_long);


        // Set the click listeners for the buttons

        tokenBtn.setOnClickListener((v) -> {
            getToken();

        });
        btnLogOut = findViewById(R.id.btnLogout);
        mAuth = FirebaseAuth.getInstance();

        btnLogOut.setOnClickListener(view ->{
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
//
//        codeBtn.setOnClickListener((v) -> {
//            getCode();
        });

        shortBtn.setOnClickListener((v) -> {
            var = "short";
            onGetUserProfileClicked();
            Toast.makeText(this, "Last month's summary!", Toast.LENGTH_SHORT).show();
        });
        mediumBtn.setOnClickListener((v) -> {
            var = "medium";
            onGetUserProfileClicked();
            Toast.makeText(this, "Last 6 month's summary!", Toast.LENGTH_SHORT).show();
        });
        longBtn.setOnClickListener((v) -> {
            var = "long";
            onGetUserProfileClicked();
            Toast.makeText(this, "Last several years' summary!", Toast.LENGTH_SHORT).show();
        });

    }

    /**
     * Get token from Spotify
     * This method will open the Spotify login activity and get the token
     * What is token?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(MainActivity.this, AUTH_TOKEN_REQUEST_CODE, request);
    }



    /**
     * Get code from Spotify
     * This method will open the Spotify login activity and get the code
     * What is code?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
//    public void getCode() {
//        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
//        AuthorizationClient.openLoginActivity(MainActivity.this, AUTH_CODE_REQUEST_CODE, request);
//    }


    /**
     * When the app leaves this activity to momentarily get a token/code, this function
     * fetches the result of that external activity to get the response from Spotify
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
//            setTextAsync(mAccessToken, tokenTextView);

        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
            //setTextAsync(mAccessCode, codeTextView);
        }
        Toast.makeText(this, "Successfully Connected!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Get user profile
     * This method will get the user profile using the token
     */
    public void onGetUserProfileClicked() {
        if (mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a request to get the user profile
        final Request requestProfile = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        // request for top artists
        final Request requestArtist = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists?time_range=" + var + "_term&offset=0")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();


        cancelCall();
        mCall = mOkHttpClient.newCall(requestProfile);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }



            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject userProfile = new JSONObject(response.body().string());

                    StringBuilder userInfo = new StringBuilder();
                    userInfo.append("YOUR SPOTIFY PROFILE: " + "\n\n\n");

                    JSONArray imagesArray = userProfile.getJSONArray("images");
                    String imageUrl = "";
                    if (imagesArray.length() > 0) {
                        imageUrl = imagesArray.getJSONObject(0).getString("url");
                    }

                    userInfo.append("Display Name: ").append(userProfile.getString("display_name")).append("\n");
                    userInfo.append("Email: ").append(userProfile.getString("email")).append("\n");
                    userInfo.append("Profile Picture: ").append(imageUrl).append("\n");
                    userInfo.append("Spotify URL: ").append(userProfile.getString("external_urls")).append("\n\n");


                    // Array for extracting data
                    userProfileArray.add(userProfile.getString("display_name"));
                    userProfileArray.add(userProfile.getString("email"));
                    userProfileArray.add(imageUrl);
                    userProfileArray.add(userProfile.getString("external_urls"));


                    setTextAsync(userInfo.toString(), profileTextView);
                    getTopArtists(requestArtist);


                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Get Top Artists
     * This method will get the top artists using the token
     */
    public void getTopArtists(Request request) {


        // request for top tracks
        final Request requestTrack = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?time_range=" + var + "_term&offset=0")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONArray itemsArray = jsonObject.getJSONArray("items");
                    int r = (int) (Math.random() * (itemsArray.length() - 1));

                    // StringBuilder to store the artists and genres
                    StringBuilder artistInfo = new StringBuilder();
                    artistInfo.append("YOUR TOP 10 ARTISTS: " + "\n\n\n");


                    for (int j = 0; j < itemsArray.length(); j++) {
                        JSONObject artistObject = itemsArray.getJSONObject(j);
                        if(j == r) {
                            recArtist = artistObject.getString("id");
                        }
                    }

                    for (int i = 0; i < Math.min(itemsArray.length(), 10); i++) {
                        JSONObject artistObject = itemsArray.getJSONObject(i);
                        ArrayList <String> artistArray = new ArrayList<>();
                        String artistName = artistObject.getString("name");

                        //get top artist id
                        favArtists.add(artistObject.getString("id"));

                        // Extract genres
                        JSONArray genresArray = artistObject.getJSONArray("genres");
                        StringBuilder genres = new StringBuilder();
                        for (int j = 0; j < genresArray.length(); j++) {
                            genres.append(genresArray.getString(j));
                            if (j < genresArray.length() - 1) {
                                genres.append(", ");
                            }
                        }

                        // Extract images
                        JSONArray imagesArray = artistObject.getJSONArray("images");
                        String imageUrl = "";
                        if (imagesArray.length() > 0) {
                            imageUrl = imagesArray.getJSONObject(0).getString("url");
                        }

                        // Append artist,images and genres to the StringBuilder
                        artistInfo.append("Artist: ").append(artistName).append("\n");
                        artistArray.add(artistName);
                        if(genres.length() == 0) {
                            artistInfo.append("Genres: Not Available ").append("\n");
                            artistArray.add("Genre Not Available");
                        } else{
                            artistInfo.append("Genres: ").append(genres).append("\n");
                            artistArray.add(genres.toString());
                        }
                        artistInfo.append("Image URL: " + "\n").append(imageUrl).append("\n\n");
                        artistArray.add(imageUrl);

                        // array for extracting data
                        topArtists.add(artistArray);
                    }

                    // Update the UI with the fetched artist information
                    setTextAsync(artistInfo.toString(), artistTextView);
                    getTopTracks(requestTrack);
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Get Top Tracks
     * This method will get the top tracks using the token
     */
    public void getTopTracks(Request request) {


        // request for related artists
        final Request requestRelArtists = new Request.Builder()
                .url("https://api.spotify.com/v1/artists/" + recArtist + "/related-artists")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();
        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONArray items = jsonObject.getJSONArray("items");


                    // StringBuilder to store the artists and songs
                    StringBuilder trackInfo = new StringBuilder();
                    trackInfo.append("YOUR TOP 10 SONGS: " + "\n\n\n");
                    for (int i = 0; i < Math.min(items.length(), 10); i++) {
                        JSONObject track = items.getJSONObject(i);

                        ArrayList<String> songArray = new ArrayList<>();

                        // Extracting track details
                        String trackName = track.getString("name");

                        JSONArray artistsArray = track.getJSONArray("artists");
                        StringBuilder artists = new StringBuilder();
                        for (int j = 0; j < artistsArray.length(); j++) {
                            JSONObject artist = artistsArray.getJSONObject(j);
                            if (j > 0) {
                                artists.append(", ");
                            }
                            artists.append(artist.getString("name"));
                        }

                        String coverImage = track.getJSONObject("album")
                                .getJSONArray("images")
                                .getJSONObject(0)
                                .getString("url");

                        String previewUrl = track.getString("preview_url");
                        topsongurl.add(previewUrl);


                        // Append artist,images, name, preview to the StringBuilder
                        trackInfo.append("Song Name: ").append(trackName).append("\n");
                        trackInfo.append("Artist(s): ").append(artists).append("\n");
                        trackInfo.append("Image URL: " + "\n").append(coverImage).append("\n");
                        trackInfo.append("Song preview: " + "\n").append(previewUrl).append("\n\n");

                        songArray.add(trackName);
                        songArray.add(artists.toString());
                        songArray.add(coverImage);
                        songArray.add(previewUrl);


                        topSongs.add(songArray);


                    }

                    // Update the UI with the fetched artist information
                    setTextAsync(trackInfo.toString(), trackTextView);
                    getRelatedArtists(requestRelArtists);

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getRelatedArtists(Request request) throws JSONException {

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseData);
                    JSONArray itemsA = jsonObject.getJSONArray("artists");

                    //pick up to 5 random related artists to recommend
                    Set<Integer> pick = new HashSet<>();
                    Random random = new Random();
                    while(pick.size() < Math.min(5, itemsA.length())) {
                        int randomNumber = random.nextInt(itemsA.length());
                        pick.add(randomNumber);
                    }

                    // StringBuilder to store the artists and genres
                    StringBuilder relatedInfo = new StringBuilder();

                    relatedInfo.append("YOUR RECOMMENDED ARTISTS: " + "\n\n\n");
                    for (int i = 0; i < itemsA.length(); i++) {
                        JSONObject artistObject = itemsA.getJSONObject(i);
                        String artistName = artistObject.getString("name");
                        String artistId = artistObject.getString("id");


                        // Extract genres
                        JSONArray genresArray = artistObject.getJSONArray("genres");
                        StringBuilder genres = new StringBuilder();
                        for (int j = 0; j < genresArray.length(); j++) {
                            genres.append(genresArray.getString(j));
                            if (j < genresArray.length() - 1) {
                                genres.append(", ");
                            }
                        }

                        // Extract images
                        JSONArray imagesArray = artistObject.getJSONArray("images");
                        String imageUrl = "";
                        if (imagesArray.length() > 0) {
                            imageUrl = imagesArray.getJSONObject(0).getString("url");
                        }

                        // Append artist,images and genres to the StringBuilder if not already a top artist and is picked
                        if(!favArtists.contains(artistId) && pick.contains(i)) {
                            ArrayList<String> recArray = new ArrayList<>();
                            relatedInfo.append("Recommended Artist: ").append(artistName).append("\n");
                            recArray.add(artistName);
                            if(genres.length() == 0) {
                                relatedInfo.append("Genres: Not Available ").append("\n");
                                recArray.add("Genres: Not Available");
                            } else{
                                relatedInfo.append("Genres: ").append(genres).append("\n");
                                recArray.add(genres.toString());
                            }
                            relatedInfo.append("Image URL: " + "\n").append(imageUrl).append("\n\n");
                            recArray.add(imageUrl);


                            recArtists.add(recArray);

                        }
                    }

                    // Update the UI with the fetched artist information
                    setTextAsync(relatedInfo.toString(), relatedTextView);
                    favArtists = new ArrayList<>();
                    Intent intent = new Intent(MainActivity.this, playsong.class);
                    intent.putStringArrayListExtra("topsongurls", topsongurl);
                    intent.putStringArrayListExtra("userProfile", userProfileArray);
                    intent.putExtra("topArtists",topArtists );
                    intent.putExtra("topSongs", topSongs);
                    intent.putExtra("recArtists", recArtists);
                    startActivity(intent);
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }





    /**
     * Creates a UI thread to update a TextView in the background
     * Reduces UI latency and makes the system perform more consistently
     *
     * @param text the text to set
     * @param textView TextView object to update
     */
    private void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

    /**
     * Get authentication request
     *
     * @param type the type of the request
     * @return the authentication request
     */
    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email", "user-top-read", "user-modify-playback-state"}) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }

    /**
     * Gets the redirect Uri for Spotify
     *
     * @return redirect Uri object
     */
    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();
    }
}