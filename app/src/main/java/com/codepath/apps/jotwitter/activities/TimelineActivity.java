package com.codepath.apps.jotwitter.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.codepath.apps.jotwitter.EndlessRecyclerViewScrollListener;
import com.codepath.apps.jotwitter.R;
import com.codepath.apps.jotwitter.adapters.TweetAdapter;
import com.codepath.apps.jotwitter.TwitterApp;
import com.codepath.apps.jotwitter.TwitterClient;
import com.codepath.apps.jotwitter.databinding.ActivityTimelineBinding;
import com.codepath.apps.jotwitter.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    private static final String TAG = "TimelineActivity";
    private static final String KEY_COMPOSE_TWEET = "tweetFromComposeActivity";
    private static final String KEY_DETAIL_ACT = "tweetForDetailActivity";


    private static final int COMPOSE_REQUEST_CODE = 1234;
    public static final int REPLY_REQUEST_CODE = 4321;

    ActivityTimelineBinding binding;
    TwitterClient client;
    List<Tweet> tweets;

    //All Views:
    RecyclerView rvTweets;
    TweetAdapter tweetAdapter;
    FloatingActionButton fabCompose;

    SwipeRefreshLayout swipeContainer;
    ProgressBar progressBar;
    Toolbar toolbar;
    private EndlessRecyclerViewScrollListener scrollListener;

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
        progressBar = binding.progressBar;
        toolbar = findViewById(R.id.toolbar);           //must be done this way as it's in a different layout file! (toolbar_main.xml)
        setSupportActionBar(toolbar);           //set the Toolbar as an actionbar
        //getSupportActionBar().setIcon(R.drawable.ic_twitter_logo);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);        //remove the title

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
                i.putExtra(ComposeActivity.KEY_PURPOSE, ComposeActivity.COMPOSE_FUNCTION);
                startActivityForResult(i, COMPOSE_REQUEST_CODE);
            }
        });

        //Set up recycler view:
        rvTweets = binding.rvTweets;
        TweetAdapter.TweetAdapterListener listener = new TweetAdapter.TweetAdapterListener() {          //Go to TweetDetailActivity!
            @Override
            public void onClick(Tweet tweetClicked) {
                Intent toDetailView = new Intent(TimelineActivity.this, TweetDetailActivity.class);
                toDetailView.putExtra(KEY_DETAIL_ACT, Parcels.wrap(tweetClicked));
                startActivity(toDetailView);
            }
        };
        tweetAdapter = new TweetAdapter(this, tweets, listener);
        rvTweets.setAdapter(tweetAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(layoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Log.d(TAG, "onLoadMore(): page give = " + page);
                loadMore();
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);

        populateHomeTimeline();
        progressBar.setVisibility(View.VISIBLE);
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess getting home timeline");

                //get the list of tweets:
                JSONArray jsonResponse = json.jsonArray;
                try {
                    tweetAdapter.clear();
                    tweetAdapter.addAll(Tweet.fromJsonArray(jsonResponse));
                    swipeContainer.setRefreshing(false);
                    progressBar.setVisibility(View.INVISIBLE);
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
    //Purpose:          To handle the results coming from ComposeActivity! Can come from an intent to Compose or to Reply! Check it using the request codes we labeled them with when we sent them!
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityReslt():      requestCode = " + requestCode + "    resultCode = " + resultCode);
        // Did this result come from Composing a new post?
        if(requestCode == COMPOSE_REQUEST_CODE && resultCode == RESULT_OK){
            Log.d(TAG, "compose activity was a success!");
            Tweet newTweet = data.getParcelableExtra(KEY_COMPOSE_TWEET);
            tweets.add(newTweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);             //scroll up to see the new tweet
        }

        //Did this result come from Replying to another user's post?:
        else if(requestCode == REPLY_REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d(TAG, "compose activity for reply was a success!");
            Tweet newTweet = Parcels.unwrap(data.getParcelableExtra(KEY_COMPOSE_TWEET));
            int tweetRepliedTo = data.getIntExtra(ComposeActivity.KEY_TWEET_POSITION, -1);
            Intent toDetailAct = new Intent(TimelineActivity.this, TweetDetailActivity.class);
            toDetailAct.putExtra(KEY_DETAIL_ACT, Parcels.wrap(newTweet));
            startActivity(toDetailAct);
        }
        else{
            Log.e(TAG, "request received did not come from the available options, OR results were NOT ok!");
        }
    }

    private void loadMore(){
        //Make network request via TwitterClient to obtain only the Tweets older than the oldest one we currently have:
        //Uses TwitterClient's getNextPageOfTweets( lastId, JsonHttpResponseHandler)
        client.getNextPageOfTweets(tweets.get(tweets.size()-1).getLongId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "loadMoreData(): onSuccess(): json response=" + json.toString());
                //1.) Get the array of Tweets:
                JSONArray jsonArray = json.jsonArray;
                try {
                    //2.) Convert into Tweet objects, append the new Tweets and notify adapter of change:
                    tweetAdapter.addAll(Tweet.fromJsonArray(jsonArray));           //addAll() notifies adapter as well
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "loadMoreData(): onFailure()", throwable);
            }
        });
    }
}