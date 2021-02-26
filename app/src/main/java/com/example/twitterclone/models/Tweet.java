package com.example.twitterclone.models;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.twitterclone.codepathResources.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Tweet.java
 * Purpose:                         Model class to obtain all needed aspects of a Tweet: id, body, User, and timestamp (createdAt). Also logged as an Entity to be put into the database
 *
 * Classes used:                    User{}, TimeFormatter{}
 *
 * @author Josephine Mai Nguyen
 * @version 2.0
 */
@Entity
public class Tweet {
    //Define database columns and associated fields:
    @PrimaryKey
    @ColumnInfo
    long id;
    @ColumnInfo
    String body;
    @ColumnInfo
    String createdAt;
    @Embedded               //To include User in the same table but still understand the two separate classes
    User user;

    public Tweet(){}

    /**
     * Purpose:         Constructor, needs a JSONObject corresponding to a Tweet
     * @param object, the JSONObject
     * @throws JSONException in the case cannot get fields from JSONObject
     */
    public Tweet(JSONObject object) throws JSONException {
        this.body = object.getString("text");
        this.createdAt = object.getString("created_at");
        this.user = new User(object.getJSONObject("user"));
        this.id = object.getLong("id");
    }

    /**
     * Purpose:         Obtains and creates a List of Tweet objects given the JSONArray that has them
     * @param jsonArray, the JSONArray
     * @throws JSONException in the case cannot get JSONObjects from jsonArray
     */
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            tweets.add(new Tweet(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    /**
     * Purpose:         Uses the TimeFormatter{} to convert the "createdAt" field into an appropriate timestamp (e.g. 8hr, 30min...)
     * @return
     */
    public String getFormattedTimestamp(){
        return TimeFormatter.getTimeDifference(this.createdAt);
    }

    //Getter methods:
    public User getUser() {
        return user;
    }
    public long getId() {
        return id;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public String getBody() {
        return body;
    }
}
