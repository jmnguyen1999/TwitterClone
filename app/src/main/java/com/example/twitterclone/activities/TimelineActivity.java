package com.example.twitterclone.activities;
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
import android.widget.Toast;

import com.example.twitterclone.codepathResources.EndlessRecyclerViewScrollListener;
import com.example.twitterclone.R;
import com.example.twitterclone.TwitterApp;
import com.example.twitterclone.network.TwitterClient;
import com.example.twitterclone.adapters.TweetsAdapter;
import com.example.twitterclone.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

/**
 * TimeLineActivity.java
 * Purpose:                         Serves as the homepage of TwitterClone app. The activity to display the user's home timeline. Allows for refreshment of most recent Tweets through SwipeRefreshLayout, endless scrolling of old Tweets through
 *                                  EndlessRecyclerViewScrollListener{}, Makes all network requests to Twitter API via TwitterClient{}
 *
 * Classes Used:                    TwitterClient{}, TweetsAdapter{}, EndlessRecyclerViewScrollListener{}, Tweet{}
 * Corresponding Layout file:       activity_timeline.xml
 *
 * @author Josephine Mai Nguyen
 * @version 2.0
 */
public class TimelineActivity extends AppCompatActivity {
    private static final String TAG = "TimelineActivity";       //for Log()
    private static final int REQUEST_CODE = 20;
    private static final String TWEET_KEY = "tweet";

    //fields:
    TwitterClient client;       //to make network requests via the Twitter API

    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter tweetsAdapter;

    SwipeRefreshLayout swipeContainer;                   //To refresh the page with new tweets
    EndlessRecyclerViewScrollListener scrollListener;       //To allow endless scrolling

    @Override
    /**
     * Purpose:         Called on when the activity is launched automatically. Initalizes all fields, sets up the RecyclerView and refresh and scroll listeners. Utilizes populateTimeline() to obtain and display Tweets.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        //1.) Initialize fields:
        client = TwitterApp.getRestClient(this);        //use the TwitterApp{} to obtain a TwitterClient{} instance tied to this activity
        tweets = new ArrayList<>();
        rvTweets = findViewById(R.id.rvTweets);
        tweetsAdapter = new TweetsAdapter(this, tweets);
        swipeContainer = findViewById(R.id.swipeContainer);

        //2.) Set up the SwipeRefreshLayout: Set color of icon, and onRefreshListener --> get new tweets
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh(): Get new data!");
                populateHomeTimeLine();
            }
        });

        //3.) Set up RecyclerView with a layout manager, an adapter, and an EndlessRecyclerViewScrollListener
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.setAdapter(tweetsAdapter);

            //3b.) Set up EndlessRecyclerViewScrollListener
            scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
                @Override
             public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    Log.i(TAG, "onLoadMore(): " + page);
                    loadMoreData();
                }
            };
        rvTweets.addOnScrollListener(scrollListener);

        //4.) Get data into List<Tweets> and display them
        populateHomeTimeLine();
    }

    @Override
    /**
     * Purpose:         Inflates the menu and adds the necessary items to it (signOff button). Functions of each item is implemented in onOptionsItemSelected(). This method is called automatically by the system
     * @return true in order for menu to display
     */
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    /**
     * Purpose:         Handlers for each item in the menu: composeBttn and signOffBttn. ComposeBttn:   navigates to ComposeActivity to create/POST a new Tweet, signOffBttn:      signs of the user and closes the activity
     * @param item, MenuItem object corresponding to an item on the menu
     * @return true to execute handler
     */
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.composeBttn:
                Intent intent = new Intent(this, ComposeActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                return true;
            case R.id.signOffBttn:
                client.clearAccessToken();
                finish();
                return true;
            default:        //item was not one of the items above
                Log.d(TAG, "onOptionsItemSelected(): itemId = " + item.getItemId());
                Toast.makeText(getApplicationContext(), "Sorry! Something went wrong!", Toast.LENGTH_SHORT).show();
                return false;
        }
    }

    @Override
    /**
     * Purpose:     Handles the request from launching the ComposeActivity in onOptionsItemSelected(). Handles by:  obtaining the new Tweet to be updated, updating the model and RV adapter
     *
     * @param requestCode is the code used to identify which specific request is responding back
     * @param resultCode is the code determining what happened during the request e.g. successful would return a RESULT_OK
     * @param data is the Intent which carries the resulting data of the request
     */
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK ){
            Tweet newTweet = Parcels.unwrap(data.getParcelableExtra(TWEET_KEY));

            //Insert Tweet:  Update List<Tweet>, insert in first spot b/c most recent tweet:
            tweets.add(0, newTweet);
            tweetsAdapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);             //scroll up to see the new tweet
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Purpose:         Handler for reaching the end of the List<Tweet> tweets to display, notified by EndlessRecyclerViewScrollListener{} field. Obtains more data by: getting the id of the last Tweet object (also = the oldest Tweet object),
     *                  making a network request to obtain only the Tweets older than that id, then re-populating and notifying the RecyclerViewAdapter
     */
    private void loadMoreData(){
        //Make network request via TwitterClient to obtain only the Tweets older than the oldest one we currently have:
        //Uses TwitterClient's getNextPageOfTweets( lastId, JsonHttpResponseHandler)
        client.getNextPageOfTweets(tweets.get(tweets.size()-1).getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "loadMoreData(): onSuccess(): json response=" + json.toString());
                //1.) Get the array of Tweets:
                JSONArray jsonArray = json.jsonArray;
                try {
                    //2.) Convert into Tweet objects, append the new Tweets and notify adapter of change:
                    tweetsAdapter.addAll(Tweet.fromJsonArray(jsonArray));           //addAll() notifies adapter as well
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

    /**
     * Purpose:     Obtains the most recent Tweets (also allows for refreshment --> is also a handler for SwipeRefreshLayout field "swipeContainer"), notifies the RecyclerView adapter
     */
    private void populateHomeTimeLine() {
        //Makes network request via TwitterClient{},
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "populateTimeLine(): onSuccess: \njson response = " + json.toString());
                //1.) Get the array of Tweets:
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweetsAdapter.clear();      //allows refreshment of page

                    //2.) Convert into Tweet objects, append the new Tweets and notify adapter of change:
                    tweetsAdapter.addAll(Tweet.fromJsonArray(jsonArray));
                    swipeContainer.setRefreshing(false);            //tells SwipeRefreshLayout's listener done refreshing
                } catch (JSONException e) {
                    Log.e(TAG, "populateHomeTimeLine(): onSuccess(): Json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "populateHomeTimeLine(): onFailure " + response, throwable);
            }
        });
    }
}