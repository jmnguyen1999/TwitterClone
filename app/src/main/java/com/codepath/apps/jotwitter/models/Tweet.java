package com.codepath.apps.jotwitter.models;

import com.codepath.apps.jotwitter.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {

    private String body;
    private String createdAt;
    private User user;
    private boolean hasMedia;
    private List<String> embeddedImages;

    public Tweet(){}        //required for Parceler

    public static Tweet fromJson(JSONObject jsonTweet) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonTweet.getString("text");
        tweet.createdAt = jsonTweet.getString("created_at");
        tweet.user = User.fromJson(jsonTweet.getJSONObject("user"));
        JSONObject entities = jsonTweet.getJSONObject("entities");

        if(entities.has("media")){
            JSONArray media = entities.getJSONArray("media");
            tweet.hasMedia = true;
            tweet.embeddedImages = new ArrayList<>();
            for(int i = 0; i < media.length(); i++){
                tweet.embeddedImages.add(media.getJSONObject(i).getString("media_url_https"));
            }
        }
        else{
            tweet.hasMedia = false;
            tweet.embeddedImages = new ArrayList<>();
        }
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
        return TimeFormatter.getRelativeTimeAgo(createdAt);
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
    public List<String> getEmbeddedImages(){
        return embeddedImages;
    }
    public boolean getHasMedia(){
        return hasMedia;
    }

}
