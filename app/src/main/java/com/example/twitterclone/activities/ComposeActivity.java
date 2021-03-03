package com.example.twitterclone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.twitterclone.R;
import com.example.twitterclone.TwitterApp;
import com.example.twitterclone.models.Tweet;
import com.example.twitterclone.network.TwitterClient;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

/**
 * ComposeActivity.java
 * Purpose:                         The activity navigated to to create and post a new Tweet object to the user's account. Obtains the user's desired tweet, with character limit restrictions with "MAX_TWEET_LENGTH" and through
 *                                  layout file: "app:counterMaxLength="#"", makes a network POST request to the Twitter API using TwitterClient{}, then returns the data to previous activity with another Intent object.
 *
 * Classes Used:                    Tweet{}, TwitterClient{}
 * Corresponding Layout file:       activity_compose.xml
 *
 * @author Josephine Mai Nguyen
 * @version 1.0
 */
public class ComposeActivity extends AppCompatActivity {
    private static final int MAX_TWEET_LENGTH = 140;
    private static final String TAG = "ComposeActivity";
    private static final String TWEET_KEY = "tweet";

    EditText etCompose;
    Button btnSubmit;
    TwitterClient client;

    @Override
    /**
     * Purpose:         Called on when the activity is launched automatically. Initializes widgets and TwitterClient{}, sets an OnClickListener to the submit button ("btnSubmit") to obtain the desired Tweet text, make a
     *                  network POST request to Twitter, create a Tweet object, and creates an Intent loaded with the Tweet object to send back to TimelineActivity{}
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etCompose = findViewById(R.id.etCompose);
        btnSubmit = findViewById(R.id.btnSubmit);

        client = TwitterApp.getRestClient(this);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validate tweet is within char range:
                String tweetContent = etCompose.getText().toString();
                if(tweetContent.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "Your tweet cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if(tweetContent.length() > MAX_TWEET_LENGTH){
                    Toast.makeText(ComposeActivity.this, "Your tweet exceeds the 140 character limit!", Toast.LENGTH_SHORT).show();
                }

                //If Tweet length is valid: POST tweet on Twitter account, and create/initialize an Intent as a response to TimelineActivity{}
                else{
                    client.publishTweet(tweetContent, new JsonHttpResponseHandler() {       //makes the API call through TwitterClient{}
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "publishTweet(): onSuccess()");
                            try {
                                Tweet newTweet = new Tweet(json.jsonObject);
                                Intent intent = new Intent();
                                intent.putExtra(TWEET_KEY, Parcels.wrap(newTweet));
                                setResult(RESULT_OK);           //tells that this activity was successful
                                finish();

                            } catch (JSONException e) {
                                Log.e(TAG, "publishTweet(): onSuccess(): error= ", e);
                                e.printStackTrace();
                            }

                        }
                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "publishTweet(): onFailure() error=", throwable);
                        }
                    });
                }
            }
        });     //end of setting the OnClickListener()
    }
}