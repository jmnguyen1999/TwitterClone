package com.codepath.apps.jotwitter.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.jotwitter.R;
import com.codepath.apps.jotwitter.TwitterApp;
import com.codepath.apps.jotwitter.TwitterClient;
import com.codepath.apps.jotwitter.databinding.ActivityComposeBinding;
import com.codepath.apps.jotwitter.models.Tweet;
import com.codepath.apps.jotwitter.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;


public class ComposeActivity extends AppCompatActivity {
    private static final String TAG = "ComposeActivity";
    public static final String KEY_PURPOSE = "are we replying or composing a new tweet?";
    public static final String KEY_REPLY_TO = "who are we replying to then?";
    public static final String KEY_TWEET_POSITION = "where in the recycler was this tweet?";        //so we can tell the recycler view to scroll to the updated Tweet later!
    private static final String KEY_COMPOSE_TWEET = "tweetFromComposeActivity";
    private static final int MAX_LENGTH = 280;

    //Constants to ensure value for KEY_PURPOSE --> one of these.
    public static final String COMPOSE_FUNCTION = "function = to compose!";
    public static final String REPLY_FUNCTION = "function = to reply!";

    ActivityComposeBinding binding;
    TwitterClient client;

    //Views:
    ImageView ivUserProfile;
    EditText etBody;
    Button btnCompose;
    RelativeLayout replyFeaturesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComposeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        client = TwitterApp.getRestClient(this);

        //1.) Connect views:
        ivUserProfile = binding.ivUserPic;
        etBody = binding.etBody;
        btnCompose = binding.btnCompose;
        replyFeaturesContainer = binding.replyFeatureContainer;

        //2.) Appropriately display the correct views + set up onClick() for btnCompose::
        // If we are replying --> set up for Reply function:
        Intent receivedData = getIntent();
        if(receivedData.getStringExtra(KEY_PURPOSE).equals(REPLY_FUNCTION)){
            setUpReplyFunctions();
        }
        else if(receivedData.getStringExtra(KEY_PURPOSE).equals(COMPOSE_FUNCTION)){
            setUpComposeFunctions();
        }
        else{
            Log.e(TAG, "Did not receive which purpose I should be doing!");
            return;
        }
        // Sets the profile image no matter what!
        getCurrentUser();
    }

    //Purpose:          Sets up the activity to only do compose functions!
    public void setUpComposeFunctions(){
        Log.d(TAG, "setUpCompose!");
        replyFeaturesContainer.setVisibility(View.GONE);        //Do not show any of the reply features

        //onclick() btnCompose --> error check the inputted string + post it!
        btnCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String attemptedBody = etBody.getText().toString();
                if(attemptedBody.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "You must input a message!", Toast.LENGTH_SHORT).show();
                }
                else if(attemptedBody.length() > MAX_LENGTH){
                    Toast.makeText(ComposeActivity.this, "Your post went over the character limit!", Toast.LENGTH_SHORT).show();
                }
                else{
                    client.publishTweet(attemptedBody, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "successful post!");
                            //1.) get the tweet out:
                            try {
                                Tweet newTweet = Tweet.fromJson(json.jsonObject);

                                Intent sendData = new Intent();
                                sendData.putExtra(KEY_COMPOSE_TWEET, Parcels.wrap(newTweet));

                                // If we were replying to someone --> need to send extra info back to TimelineActivity!
                                Intent receivedData = getIntent();
                                if(receivedData.getStringExtra(KEY_PURPOSE).equals(REPLY_FUNCTION)){
                                    //Send TimelineActivity the position of the Tweet passed to us from the TweetAdapter: (so yeah we don't do anything with the position it's just for it to be communicated to the TimelineActivity!)
                                    sendData.putExtra(KEY_TWEET_POSITION, receivedData.getIntExtra(KEY_TWEET_POSITION, -1));
                                }
                                setResult(RESULT_OK, sendData);
                                finish();
                            } catch (JSONException e) {
                                Log.e(TAG, "Error parsing the jsonObject=", e);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "unsuccessful post :(", throwable);
                        }
                    });
                }
            }
        });
    }

    //Purpose:      Sets up the activity to do reply functions!
    public void setUpReplyFunctions(){
        Log.d(TAG, "setUpReply!");
        //1.) Get the Tweet and necessary info:
        Intent receivedData = getIntent();
        Tweet tweetReplyTo = Parcels.unwrap(receivedData.getParcelableExtra(KEY_REPLY_TO));
        String replyToUsername = tweetReplyTo.getUser().getUsername();
        String replyTweetId = tweetReplyTo.getId();                              //used to post the reply!

        //2.) Populate views:
        TextView tvReplyTo = binding.tvWhoReply;
        tvReplyTo.setText("@"+ replyToUsername);
        etBody.setText("@" + replyToUsername + " ");              //autofill the @username in the body text field

        //3.) onclick() compose button --> error check the body
        btnCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //3a.) Error check the input:
                String attemptedBody = etBody.getText().toString();
                if(attemptedBody.length() == replyToUsername.length()+1){
                    Toast.makeText(ComposeActivity.this, "You must input a message!", Toast.LENGTH_SHORT).show();
                }
                else if(attemptedBody.length() > MAX_LENGTH){
                    Toast.makeText(ComposeActivity.this, "Your post went over the character limit!", Toast.LENGTH_SHORT).show();
                }

                //3b.) We good --> try to post the reply: uses the replyTweet's id
                else{
                    client.postReply(replyTweetId, attemptedBody, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "successful posting the reply!");
                            //1.) get the tweet out:
                            try {
                                Tweet newTweet = Tweet.fromJson(json.jsonObject);

                                Intent sendData = new Intent();
                                sendData.putExtra(KEY_COMPOSE_TWEET, Parcels.wrap(newTweet));           //send back the new tweet

                                //Send back the position of the Tweet passed to us from the TweetAdapter: (so yeah we don't do anything with the position it's just for it to be communicated to the TimelineActivity!)
                                sendData.putExtra(KEY_TWEET_POSITION, receivedData.getIntExtra(KEY_TWEET_POSITION, -1));
                                setResult(RESULT_OK, sendData);
                                finish();
                            } catch (JSONException e) {
                                Log.e(TAG, "Error parsing the jsonObject=", e);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "Error posting reply = ", throwable);
                        }
                    });
                }
            }
        });
    }


    //Purpose:      Obtains the current user's profile url + loads it!
    public void getCurrentUser(){
        client.getCurrentUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess getting current user profile!");
                JSONObject jsonResponse = json.jsonObject;
                try {
                    //user = User.fromJson(jsonResponse);
                    //Log.d(TAG, "Name = " + jsonResponse.getString("name"));
                    Log.d(TAG, "profile url = " + jsonResponse.getString("profile_image_url_https"));
                    Glide.with(ComposeActivity.this).load(jsonResponse.getString("profile_image_url_https")).into(ivUserProfile);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure error=", throwable);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return true;            //show the menu
    }

    @Override
    //Purpose:      handlers for each menu item
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.publishTweetMenuItem){
            Toast.makeText(ComposeActivity.this, "post button clicked!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}