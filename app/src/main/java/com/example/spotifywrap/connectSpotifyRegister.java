package com.example.spotifywrap;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;

public class connectSpotifyRegister extends AppCompatActivity {

    public static final String CLIENT_ID = "8852c891d90a44e8be613f09ded6d8b3";
    public static final String REDIRECT_URI = "spotifywrap://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken,mAccessCode;
    private Call mCall;
    String username;
    String email;
    private Button connect;
    private Button confirm;
    private TextView uname;
    private TextView back;

    private String token;





    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_connect_spotify);

        connect = (Button) findViewById(R.id.btnConnect);
        back = (TextView) findViewById(R.id.tvBack);
        confirm = findViewById(R.id.btnConfirm);
        uname = findViewById(R.id.etChangeUname);

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String UID = user.getUid(); // firebase user id
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // db of wrapped for each user
        DocumentReference docRef = db.collection("wraplify").document(UID);



        // Set the click listeners for the buttons

        connect.setOnClickListener((v) -> {
            getToken();

        });
        confirm.setOnClickListener((v) -> {

            // Get the text from the TextView
            username = uname.getText().toString();
            //Store username to firebase here
            docRef.update("username", username);

            // Check if the text is not empty
            if (!username.isEmpty()) {

                Toast.makeText(connectSpotifyRegister.this, "Username successfully updated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(connectSpotifyRegister.this, "Enter a new username", Toast.LENGTH_SHORT).show();
            }


        });


        back.setOnClickListener(view ->{
            if (mAccessToken == null || username == null){
                Toast.makeText(this, "You need to connect a Spotify account and create an username to finish setup.", Toast.LENGTH_SHORT).show();
            } else {
                // store mAccesstoken in firebase here
                CollectionReference colRef = db.collection("wraplify");
                Map<String, Object> docData = new HashMap<>();
                docData.put("spotifyId", mAccessToken);
                docData.put("username", username);

                colRef.document(UID).set(docData);

                startActivity(new Intent(connectSpotifyRegister.this, LoginActivity.class));
            }

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
        AuthorizationClient.openLoginActivity(connectSpotifyRegister.this, AUTH_TOKEN_REQUEST_CODE, request);
    }





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
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();
    }

}
