package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Tweet {
    public String body;
    public String createdAt;
    public User user;
    public long id;

    //given a json object, turn it into a Tweet object
    public static Tweet fromJsonObject(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJsonObject(jsonObject.getJSONObject("user"));
        tweet.id = jsonObject.getLong("id");

        return tweet;
    }

    //Obtains and creates a List of Tweet objects given the json array that has them
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            tweets.add(fromJsonObject(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public static String getFormattedTimestamp(String createdAt){
        return TimeFormatter.getTimeDifference(createdAt);

    }

}
