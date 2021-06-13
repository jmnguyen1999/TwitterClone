package com.codepath.apps.jotwitter.models;

import com.codepath.apps.jotwitter.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Tweet {

    private String body;
    private String createdAt;
    private User user;

    public static Tweet fromJson(JSONObject jsonTweet) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonTweet.getString("text");
        tweet.createdAt = jsonTweet.getString("created_at");
        tweet.user = User.fromJson(jsonTweet.getJSONObject("user"));
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonAllTweets) throws JSONException {
        List<Tweet> allTweets = new ArrayList<>();
        for(int i =  0; i < jsonAllTweets.length(); i++){
            allTweets.add(Tweet.fromJson(jsonAllTweets.getJSONObject(i)));
        }
        return allTweets;
    }

    public String getFormattedTimestamp(){
        return TimeFormatter.getTimeDifference(createdAt);
    }
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
    public User getUser(){
        return user;
    }
    public String getCreatedAt(){
        return createdAt;
    }

}
