//package com.codepath.apps.restclienttemplate;
package com.example.twitterclone;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.twitterclone.R;
import com.example.twitterclone.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    private static final String TAG = "TimelineActivity";       //for Log()

    //fields:
    TwitterClient client;
    Button signOffBttn;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);


        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh(): Getting new data!");
                populateHomeTimeLine();
            }
        });
        client = TwitterApp.getRestClient(this);
        tweets = new ArrayList<>();

        //Set up RecyclerView with List of Tweet objects and TweetAdapter
        rvTweets = findViewById(R.id.rvTweets);
        adapter = new TweetsAdapter(this, tweets);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(layoutManager);
        rvTweets.setAdapter(adapter);

        scrollListener = new
                EndlessRecyclerViewScrollListener(layoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        Log.i(TAG, "onLoadMore(): " + page);
                        loadMoreData();
                    }
                };

        rvTweets.addOnScrollListener(scrollListener);

        /*signOffBttn = findViewById(R.id.signOffBttn);
        signOffBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.clearAccessToken();
                finish();
            }
        });*/
        populateHomeTimeLine();
    }

    //Inflates the signoff button on menu
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.signOffBttn){
            client.clearAccessToken();
            finish();
            return true;
        }
        else{
            Log.d(TAG, "onOptionsItemSelected(): itemId = " + item.getItemId());
            Toast.makeText(getApplicationContext(), "Sorry! Something went wrong with the Sign Off button!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    private void loadMoreData() {
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "loadMoreData(): onSuccess(): json response=" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    List<Tweet> tweets = Tweet.fromJsonArray(jsonArray);

                    //append new data objects and notify adapter:
                    adapter.addAll(tweets);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "loadMoreData(): onFailure()", throwable);
            }
        }, tweets.get(tweets.size()-1).id);
    }

    private void populateHomeTimeLine() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "populateTimeLine(): onSuccess: \njson response = " + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    adapter.clear();
                    adapter.addAll(Tweet.fromJsonArray(jsonArray));
                    swipeContainer.setRefreshing(false);    //tell SwipeRefreshLayout's listener done refreshing
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