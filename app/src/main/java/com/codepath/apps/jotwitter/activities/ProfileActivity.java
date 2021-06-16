package com.codepath.apps.jotwitter.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.jotwitter.R;
import com.codepath.apps.jotwitter.TwitterApp;
import com.codepath.apps.jotwitter.adapters.TweetAdapter;
import com.codepath.apps.jotwitter.databinding.ActivityProfileBinding;
import com.codepath.apps.jotwitter.models.Tweet;
import com.codepath.apps.jotwitter.models.User;
import com.codepath.apps.jotwitter.network.TwitterClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class ProfileActivity extends AppCompatActivity{ /*implements AppBarLayout.OnOffsetChangedListener {*/
    private static final String TAG = "ProfileActivity";

    ActivityProfileBinding binding;
    TwitterClient client;
    User user;

    ImageView ivBackdrop;
    ImageView ivProfilePic;
    TextView tvName;
    TextView tvUsername;
    TextView tvBio;
    TextView tvFollowing;
    TextView tvFollowers;

    Button btnTweets;
    Button btnTweetsAndReplies;
    Button btnMedia;
    Button btnLikes;
    RecyclerView rvTweets;
    TweetAdapter tweetAdapter;

    List<Tweet> tweets;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        client = TwitterApp.getRestClient(this);

        ivBackdrop = binding.ivBackdrop;
        ivProfilePic = binding.ivProfilePic;
        tvName = binding.tvName;
        tvUsername = binding.tvUsername;
        tvBio = binding.tvBio;
        tvFollowing = binding.tvFollowing;
        tvFollowers = binding.tvFollowers;
        btnTweets = binding.btnTweets;
        btnTweetsAndReplies = binding.btnTweetsReplies;
        btnMedia = binding.btnMedia;
        btnLikes = binding.btnLikes;
        rvTweets = binding.rvTweets;

        tweets = new ArrayList<>();
        TweetAdapter.TweetAdapterListener listener = new TweetAdapter.TweetAdapterListener() {
            @Override
            public void onTweetClick(Tweet tweetClicked) {

            }

            @Override
            public void onCommentClick(Tweet tweetClicked) {

            }

            @Override
            public void onHeartClick(Tweet tweetClicked, ImageView heartIcon) {

            }

            @Override
            public void onProfilePicClicked(Tweet tweetClicked) {

            }

            @Override
            public void onRetweetClicked(Tweet tweetClicked, ImageView retweetIcon) {

            }
        };
        tweetAdapter = new TweetAdapter(this, tweets, listener);
        rvTweets.setAdapter(tweetAdapter);
        rvTweets.setLayoutManager(new LinearLayoutManager(this));

        populateViews();
    }

    //Purpose:      Obtains the current user's profile url + loads it!
    public void populateViews(){
        client.getCurrentUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess getting current user profile!");
                JSONObject jsonResponse = json.jsonObject;
                try {
                    //user = User.fromJson(jsonResponse);
                    //Log.d(TAG, "Name = " + jsonResponse.getString("name"));
                    Log.d(TAG, "profile url = " + jsonResponse.getString("profile_image_url_https"));
                    Glide.with(ProfileActivity.this)
                            .load(jsonResponse.getString("profile_image_url_https"))
                            .centerCrop() // scale image to fill the entire ImageView
                            .transform(new CircleCrop())
                            .into(ivProfilePic);
                    Glide.with(ProfileActivity.this)
                            .load(jsonResponse.getString("profile_background_image_url_https"))
                            .centerCrop() // scale image to fill the entire ImageView
                            .transform(new CircleCrop())
                            .into(ivBackdrop);
                    tvName.setText(jsonResponse.getString("name"));
                    tvUsername.setText(jsonResponse.getString("screen_name"));
                    tvBio.setText(jsonResponse.getString("description"));
                    Log.d(TAG, "bio = " + jsonResponse.getString("description"));
                    setUserId(jsonResponse.getString("id_str"));
                    getUserTweets();
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

    public void setUserId(String id){
        userId = id;
    }
    public void getUserTweets(){
        client.getUserTweets(userId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
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

            }
        });
    }


}