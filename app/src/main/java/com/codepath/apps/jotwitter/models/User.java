package com.codepath.apps.jotwitter.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity
public class User {
    @ColumnInfo
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo
    private String username;
    @ColumnInfo
    private String profileUrl;
    @ColumnInfo
    private String name;
    @ColumnInfo
    private String bio;


    public User(){}

    public static User fromJson(JSONObject jsonUser) throws JSONException {
        User user = new User();
        user.name = jsonUser.getString("name");
        user.username = jsonUser.getString("screen_name");
        user.profileUrl = jsonUser.getString("profile_image_url_https");
        user.bio = jsonUser.getString("description");
        user.id = jsonUser.getString("id_str");
        return user;
    }

    public static List<User> fromTweetList(List<Tweet> tweets){
        List<User> users = new ArrayList<>();
        for(int i = 0; i < tweets.size(); i++){
            users.add(tweets.get(i).user);
        }
        return users;
    }
    public String getUsername(){
        return username;
    }
    public String getProfileUrl(){
        return profileUrl;
    }
    public String getName(){
        return name;
    }
    public String getBio(){return bio;}
    public String getId(){return id;}

    public void setId(String id) {
        this.id = id;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }
}
