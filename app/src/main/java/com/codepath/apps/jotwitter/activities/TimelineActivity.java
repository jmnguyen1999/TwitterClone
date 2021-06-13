package com.codepath.apps.jotwitter.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.jotwitter.R;
import com.codepath.apps.jotwitter.TweetAdapter;
import com.codepath.apps.jotwitter.TwitterApp;
import com.codepath.apps.jotwitter.TwitterClient;
import com.codepath.apps.jotwitter.databinding.ActivityTimelineBinding;
import com.codepath.apps.jotwitter.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    private static final String TAG = "TimelineActivity";

    ActivityTimelineBinding binding;

    TwitterClient client;
    RecyclerView rvTweets;
    TweetAdapter tweetAdapter;
    List<Tweet> tweets;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimelineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tweets = new ArrayList<>();
        client = TwitterApp.getRestClient(this);

        //Set up recycler view:
        rvTweets = binding.rvTweets;
        tweetAdapter = new TweetAdapter(this, tweets);
        rvTweets.setAdapter(tweetAdapter);
        rvTweets.setLayoutManager(new LinearLayoutManager(this));

        populateHomeTimeline();
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess");

                //get the list of tweets:
                JSONArray jsonResponse = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonResponse));
                    tweetAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure, e=", throwable);
            }
        });
    }
}