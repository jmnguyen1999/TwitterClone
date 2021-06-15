package com.codepath.apps.jotwitter.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.codepath.apps.jotwitter.otherfeatures.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"))
public class Tweet {
    @ColumnInfo
    @PrimaryKey
    @NonNull
    private String id;
    @ColumnInfo
    private long longId;

    @ColumnInfo
    private String body;
    @ColumnInfo
    private String createdAt;
    @ColumnInfo
    private boolean hasMedia;
    @Ignore
    private List<String> embeddedImages;
    @ColumnInfo
    private String firstEmbeddedImage;

    @Ignore
    public User user;
    @ColumnInfo
    private String userId;

    public Tweet(){}        //required for Parceler

    public static Tweet fromJson(JSONObject jsonTweet) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonTweet.getString("text");
        tweet.createdAt = jsonTweet.getString("created_at");
        tweet.user = User.fromJson(jsonTweet.getJSONObject("user"));
        tweet.id = jsonTweet.getString("id_str");
        tweet.longId = jsonTweet.getLong("id");
        tweet.userId = tweet.user.getId();

        JSONObject entities = jsonTweet.getJSONObject("entities");
        if(entities.has("media")){
            JSONArray media = entities.getJSONArray("media");
            tweet.hasMedia = true;
            tweet.embeddedImages = new ArrayList<>();
            for(int i = 0; i < media.length(); i++){
                tweet.embeddedImages.add(media.getJSONObject(i).getString("media_url_https"));
            }
            tweet.firstEmbeddedImage = tweet.embeddedImages.get(0);
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
    public void setUser(User newUser){
        user = newUser;
    }
    public String getCreatedAt(){
        return createdAt;
    }
    public void setCreatedAt(String newCreatedAt){ createdAt = newCreatedAt;}

    public ArrayList<String> getEmbeddedImages(){
        ArrayList<String> images = new ArrayList<>();
        images.addAll(embeddedImages);
        return images;
    }
    public void setEmbeddedImages(ArrayList<String> newImages){
        embeddedImages.clear();
        embeddedImages.addAll(newImages);
    }
    public boolean getHasMedia(){
        return hasMedia;
    }
    public void setHasMedia(boolean answer){
        hasMedia = answer;
    }
    public String getId(){
        return id;
    }
    public void setId(String newId){id = newId;}

    public long getLongId(){ return longId;}
    public void setLongId(long newLongId){longId = newLongId;}

    public String getFirstEmbeddedImage(){ return firstEmbeddedImage;}
    public void setFirstEmbeddedImage(String newUrl){
        firstEmbeddedImage = newUrl;
    }
    public String getUserId(){
        return userId;
    }
    public void setUserId(String newId){
        userId = newId;
    }
}
