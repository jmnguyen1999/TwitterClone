package com.codepath.apps.jotwitter.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.apps.jotwitter.fragments.ComposeDialog;
import com.codepath.apps.jotwitter.otherfeatures.EndlessRecyclerViewScrollListener;
import com.codepath.apps.jotwitter.R;
import com.codepath.apps.jotwitter.adapters.TweetAdapter;
import com.codepath.apps.jotwitter.TwitterApp;
import com.codepath.apps.jotwitter.network.TwitterClient;
import com.codepath.apps.jotwitter.databinding.ActivityTimelineBinding;
import com.codepath.apps.jotwitter.models.Tweet;
import com.codepath.apps.jotwitter.models.TweetWithUser;
import com.codepath.apps.jotwitter.models.TwitterDao;
import com.codepath.apps.jotwitter.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity implements ComposeDialog.Listener{
    private static final String TAG = "TimelineActivity";
    private static final String KEY_COMPOSE_TWEET = "tweetFromComposeActivity";
    private static final String KEY_DETAIL_ACT = "tweetForDetailActivity";


    private static final int COMPOSE_REQUEST_CODE = 1234;
    public static final int REPLY_REQUEST_CODE = 4321;

    ActivityTimelineBinding binding;
    TwitterClient client;
    TwitterDao tweetDao;

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
        tweetDao = ((TwitterApp) getApplicationContext()).getMyDatabase().tweetDao();
        fabCompose = binding.fabCompose;
        swipeContainer = binding.swipeContainer;
        progressBar = binding.progressBar;
        toolbar = findViewById(R.id.toolbar);           //must be done this way as it's in a different layout file! (toolbar_main.xml)
        setSupportActionBar(toolbar);           //set the Toolbar as an actionbar
        //getSupportActionBar().setIcon(R.drawable.ic_twitter_logo);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);        //remove the title

        progressBar.setVisibility(View.VISIBLE);
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
                FragmentManager fragmentManager = getSupportFragmentManager();
                ComposeDialog composeDialog = ComposeDialog.newInstance(ComposeDialog.COMPOSE_FUNCTION, null);
                composeDialog.show(fragmentManager, "dialog_compose");
            }
        });

        //Set up recycler view:
        rvTweets = binding.rvTweets;
        TweetAdapter.TweetAdapterListener listener = initalizeAdapterListener();
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

        //Query for existing Tweets in the DB:
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "showing data from database");
                List<TweetWithUser> tweetWithUsers = tweetDao.recentItems();
                List<Tweet> tweetsFromDB = TweetWithUser.getTweetList(tweetWithUsers);
                tweetAdapter.addAll(tweetsFromDB);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        populateHomeTimeline();
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
                    List<Tweet> tweetsFromNetwork = Tweet.fromJsonArray(jsonResponse);
                    tweetAdapter.addAll(tweetsFromNetwork);
                    swipeContainer.setRefreshing(false);
                    progressBar.setVisibility(View.INVISIBLE);

                    //Query for existing Tweets in the DB:
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "saving data into database");
                            //Insert users first (b/c Tweets require a userId):
                            List<User> usersFromNetwork = User.fromTweetList(tweetsFromNetwork);
                            tweetDao.insertMode(usersFromNetwork.toArray(new User[0]));
                            //Insert tweets after
                            tweetDao.insertModel(tweetsFromNetwork.toArray(new Tweet[0]));      //this new array DOES automatically size itself!
                        }
                    });

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

    @Override
    //Purpose:      update the adapter to show this new tweet on the timeline!
    public void composeFinished(Tweet newTweet) {
        Log.d(TAG, "compose dialog for compose was a success!");
        tweets.add(newTweet);
        tweetAdapter.notifyItemInserted(0);
        rvTweets.smoothScrollToPosition(0);             //scroll up to see the new tweet
    }

    @Override
    //Purpose:      Go to detail view to show all replies from this tweet
    public void replyFinished(Tweet tweetRepliedTo) {
        Log.d(TAG, "compose dialog for reply was a success!");
        Intent toDetailAct = new Intent(TimelineActivity.this, TweetDetailActivity.class);
        toDetailAct.putExtra(KEY_DETAIL_ACT, Parcels.wrap(tweetRepliedTo));
        startActivity(toDetailAct);
    }

    public TweetAdapter.TweetAdapterListener initalizeAdapterListener(){
        TweetAdapter.TweetAdapterListener listener = new TweetAdapter.TweetAdapterListener() {          //Go to TweetDetailActivity!
            @Override
            public void onTweetClick(Tweet tweetClicked) {
                Intent toDetailView = new Intent(TimelineActivity.this, TweetDetailActivity.class);
                toDetailView.putExtra(KEY_DETAIL_ACT, Parcels.wrap(tweetClicked));
                startActivity(toDetailView);
            }

            @Override
            //Purpose:      --> need to reply! Create ComposeFragment!
            public void onCommentClick(Tweet tweetClicked) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                ComposeDialog composeDialog = ComposeDialog.newInstance(ComposeDialog.REPLY_FUNCTION, tweetClicked);
                composeDialog.show(fragmentManager, "dialog_compose");
            }

            @Override
            //Purpose:   --> favorite this tweet!
            public void onHeartClick(Tweet tweetClicked, ImageView heartIcon) {
                if(!heartIcon.isSelected()) {
                    Log.d(TAG, "heartIcon is selected = false");
                    client.favoriteThisTweet(tweetClicked.getLongId(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "success in liking the tweet!");
                            heartIcon.setImageDrawable(getDrawable(R.drawable.ic_vector_heart));
                            heartIcon.setSelected(true);
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "error liking the tweet =", throwable);
                        }
                    });
                }
                else{
                    Log.d(TAG, "heartIcon is selected = true");
                    client.unfavoriteThisTweet(tweetClicked.getLongId(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            heartIcon.setImageDrawable(getDrawable(R.drawable.ic_vector_heart_stroke));
                            heartIcon.setSelected(false);
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "failed to unfavorite tweet = " + response, throwable);
                        }
                    });
                }
            }

            @Override
            //Purpose:      Show followers --> go to FollowerActivity
            public void onProfilePicClicked(Tweet tweetClicked) {
                Intent toFollowerAct = new Intent(TimelineActivity.this, FollowerActivity.class);
                toFollowerAct.putExtra(FollowerActivity.KEY_USER, Parcels.wrap(tweetClicked.getUser()));
                startActivity(toFollowerAct);
            }

            @Override
            public void onRetweetClicked(Tweet tweetClicked, ImageView retweetIcon) {
                //If it's not yet selected --> select it:
                if(!retweetIcon.isSelected()) {
                    Log.d(TAG, "retweetIcon is selected = false");
                    retweetIcon.setImageDrawable(getDrawable(R.drawable.ic_vector_retweet));
                    retweetIcon.setSelected(true);
                    client.retweet(tweetClicked.getLongId(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Toast.makeText(TimelineActivity.this, "successfully retweeted!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Toast.makeText(TimelineActivity.this, "failed to retweet :(", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "failed to retweet = " + response, throwable);
                        }
                    });
                }
                //Otherwise --> is selected --> unselect it
                else{
                    Log.d(TAG, "retweetIcon is selected = true");
                    retweetIcon.setImageDrawable(getDrawable(R.drawable.ic_vector_retweet_stroke));
                    retweetIcon.setSelected(false);

                    client.unretweet(tweetClicked.getLongId(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "successfully unretweeted");
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "failed to unretweet: " + response, throwable);
                        }
                    });
                }
            }
        };
        return listener;
    }
}