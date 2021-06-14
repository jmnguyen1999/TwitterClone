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
    private static final String KEY_COMPOSE_TWEET = "tweetFromComposeActivity";
    private static final int MAX_LENGTH = 280;
    
    ActivityComposeBinding binding;
    TwitterClient client;
    User user;

    ImageView ivUserProfile;
    EditText etBody;
    Button btnCompose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComposeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        client = TwitterApp.getRestClient(this);

        ivUserProfile = binding.ivUserPic;
        etBody = binding.etBody;
        btnCompose = binding.btnCompose;

        getCurrentUser();

        btnCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String attemptedBody = etBody.getText().toString();
                if(attemptedBody.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "You must input a message!", Toast.LENGTH_SHORT).show();
                }
                else if(attemptedBody.length() > MAX_LENGTH){

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

    public void getCurrentUser(){
        client.getCurrentUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess()");
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