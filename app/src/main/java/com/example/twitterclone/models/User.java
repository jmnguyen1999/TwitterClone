package com.example.twitterclone.models;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * User.java
 * Purpose:                         Model class to obtain all needed aspects of a User: name, username (screenName), and profile image. Also logged as an Entity to be put into the database
 *
 * Classes used:                    Tweet{}
 *
 * @author Josephine Mai Nguyen
 * @version 1.0
 */
@Entity
@Parcel
public class User {
    @ColumnInfo
    @PrimaryKey
    long id;
    @ColumnInfo
    String name;
    @ColumnInfo
    String screenName;
    @ColumnInfo
    String profileImageUrl;

    public User(){}

    /**
     * Purpose:         Constructor, given a JSONObject that carries user information.
     * @param object the json object that should be a user definition from Twitter's API
     * @throws JSONException in the case cannot get fields from JSONObject
     */
    public User(JSONObject object) throws JSONException {
        this.name = object.getString("name");
        this.screenName = object.getString("screen_name");
        this.profileImageUrl = object.getString("profile_image_url_https");
        this.id = object.getLong("id");
    }

    /**
     * Purpose:         Returns the List of Users given a List of Tweets
     * @param tweets, List<Tweet>
     * @return users, List<User> extracted from tweets
     */
    public static List<User> fromTweetList(List<Tweet> tweets){
        List<User> users = new ArrayList<>();
        for(int i = 0; i < tweets.size(); i++){
            users.add(tweets.get(i).getUser());
        }
        return users;
    }

    public String getName() {
        return name;
    }
    public String getScreenName() {
        return screenName;
    }
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public long getId() { return id; }
}
