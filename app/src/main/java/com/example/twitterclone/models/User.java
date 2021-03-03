package com.example.twitterclone.models;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * User.java
 * Purpose:                         Model class to obtain all needed aspects of a User: name, username (screenName), and profile image. Also logged as an Entity to be put into the database
 *
 * Classes used:                    None
 *
 * @author Josephine Mai Nguyen
 * @version 1.0
 */
@Entity
@Parcel
public class User {
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
}
