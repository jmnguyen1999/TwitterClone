package com.example.twitterclone.models;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.twitterclone.codepathResources.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

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
@Parcel
@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"))
public class Tweet {
    //Define database columns and associated fields:
    @PrimaryKey
    @ColumnInfo
    long id;
    @ColumnInfo
    String body;
    @ColumnInfo
    String createdAt;
    @ColumnInfo
    long userId;        //To use as a foreign key
    @Ignore
    User user;

    public Tweet(){}    //needed by Parceler library

    /**
     * Purpose:         Constructor, needs a JSONObject corresponding to a Tweet
     * @param object, the JSONObject
     * @throws JSONException in the case cannot get fields from JSONObject
     */
    public Tweet(JSONObject object) throws JSONException {
        body = object.getString("text");
        createdAt = object.getString("created_at");
        user = new User(object.getJSONObject("user"));
        id = object.getLong("id");
        userId = user.getId();
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
