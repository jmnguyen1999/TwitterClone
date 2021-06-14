package com.codepath.apps.jotwitter.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.jotwitter.R;
import com.codepath.apps.jotwitter.TweetAdapter;
import com.codepath.apps.jotwitter.TwitterApp;
import com.codepath.apps.jotwitter.TwitterClient;
import com.codepath.apps.jotwitter.databinding.ActivityTimelineBinding;
import com.codepath.apps.jotwitter.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    private static final String TAG = "TimelineActivity";
    private static final String KEY_COMPOSE_TWEET = "tweetFromComposeActivity";
    private static final int COMPOSE_REQUEST_CODE = 1234;

    ActivityTimelineBinding binding;

    TwitterClient client;
    RecyclerView rvTweets;
    TweetAdapter tweetAdapter;
    List<Tweet> tweets;
    FloatingActionButton fabCompose;
    SwipeRefreshLayout swipeContainer;

   // BottomNavigationView bottomNavBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimelineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tweets = new ArrayList<>();
        client = TwitterApp.getRestClient(this);
        fabCompose = binding.fabCompose;
        swipeContainer = binding.swipeContainer;
        /* This would require us to use Fragments! So let's not implement it for the sake of following the assignment!
        bottomNavBar = binding.bottomNavigation;

        //Set up bottom navigation:
        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:

                        return true;
                    case R.id.action_search:
                        // do something here
                        return true;
                    case R.id.action_notification:
                        // do something here
                        return true;
                    case R.id.action_message:
                        // do something here
                        return true;
                    default: return true;
                }
            }
        });
        bottomNavBar.setSelectedItemId(R.id.action_home);           //display home */

        //Set up swipeContainer:
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateHomeTimeline();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        //Set up fabCompose:
        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
                startActivityForResult(i, COMPOSE_REQUEST_CODE);
            }
        });

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
                    tweetAdapter.clear();
                    tweetAdapter.addAll(Tweet.fromJsonArray(jsonResponse));
                    swipeContainer.setRefreshing(false);
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

    @Override
    //Purpose:          To handle the result from ComposeActivity!
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == COMPOSE_REQUEST_CODE && resultCode == RESULT_OK){
            Log.d(TAG, "compose activity was a success!");
            Tweet newTweet = data.getParcelableExtra(KEY_COMPOSE_TWEET);
            tweets.add(newTweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);             //scroll up to see the new tweet
        }
    }
}