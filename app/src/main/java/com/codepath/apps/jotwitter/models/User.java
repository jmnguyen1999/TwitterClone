package com.codepath.apps.jotwitter.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.io.File;

@Parcel
public class User {
    private String username;
    private String profileUrl;
    private String name;
    private String bio;
    private String id;

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
}
