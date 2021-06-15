package com.codepath.apps.jotwitter.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.codepath.apps.jotwitter.R;
import com.codepath.apps.jotwitter.TwitterApp;
import com.codepath.apps.jotwitter.TwitterClient;
import com.codepath.apps.jotwitter.adapters.FollowerAdapter;
import com.codepath.apps.jotwitter.databinding.ActivityFollowerBinding;
import com.codepath.apps.jotwitter.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

//Purpose:          Displays the followers and following attributes of the given user.
public class FollowerActivity extends AppCompatActivity {
    private static final String TAG = "FollowerActivity";
    public static final String KEY_USER = "which user?";

    User user;
    ActivityFollowerBinding binding;
    List<User> users;
    FollowerAdapter followerAdapter;

    //Views:
    Button btnFollowers;
    View selectFollowersLine;
    Button btnFollowing;
    View selectFollowingLine;
    RecyclerView rvFollowers;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFollowerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //1.) Get the user we are looking at:
        Intent receivedData = getIntent();
        user = Parcels.unwrap(receivedData.getParcelableExtra(KEY_USER));

        client = TwitterApp.getRestClient(this);
        users = new ArrayList<>();

        //2.) Connect Views:
        btnFollowers = binding.btnFollowers;
        btnFollowing = binding.btnFollowing;
        selectFollowersLine = binding.selectFollowers;
        selectFollowingLine = binding.selectFollowing;
        rvFollowers = binding.rvFollowers;

        followerAdapter = new FollowerAdapter(this, users);
        rvFollowers.setAdapter(followerAdapter);
        rvFollowers.setLayoutManager(new LinearLayoutManager(this));

        //By default we start on the followers page:
        getFollowers();

        //3.) Set on click listeners:
        btnFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFollowers();
                selectFollowersLine.setVisibility(View.VISIBLE);
                selectFollowingLine.setVisibility(View.INVISIBLE);
            }
        });

        btnFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFollowing();
                selectFollowingLine.setVisibility(View.VISIBLE);
                selectFollowersLine.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void getFollowers(){

        client.getFollowers(user.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "success in getting followers");
                JSONObject jsonResponse = json.jsonObject;
                try{
                    users.clear();
                    followerAdapter.notifyDataSetChanged();

                    JSONArray allUsers = jsonResponse.getJSONArray("users");
                    for(int i = 0; i < allUsers.length(); i++){
                        users.add(User.fromJson(allUsers.getJSONObject(i)));
                    }
                    followerAdapter.notifyDataSetChanged();
                }catch(JSONException e){
                    Log.e(TAG, "error getting user json array = ", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });
    }

    public void getFollowing(){
        client.getFollowing(user.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "success in getting following");
                JSONObject jsonResponse = json.jsonObject;
                try{
                    users.clear();
                    followerAdapter.notifyDataSetChanged();

                    JSONArray allUsers = jsonResponse.getJSONArray("users");
                    for(int i = 0; i < allUsers.length(); i++){
                        users.add(User.fromJson(allUsers.getJSONObject(i)));
                    }
                    followerAdapter.notifyDataSetChanged();
                }catch(JSONException e){
                    Log.e(TAG, "error getting user json array = ", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });
    }



}