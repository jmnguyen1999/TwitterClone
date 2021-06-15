package com.codepath.apps.jotwitter.models;

import androidx.room.Embedded;

import java.util.ArrayList;
import java.util.List;

public class TweetWithUser {


    @Embedded   //Embed every field in the user object into this user
    User user;

    @Embedded(prefix = "tweet_")     //name every field with "tweet" at beginning of it
    Tweet tweet;

    public static List<Tweet> getTweetList(List<TweetWithUser> tweetWithUser){
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < tweetWithUser.size(); i++){
            Tweet tweet = tweetWithUser.get(i).tweet;
            tweet.user = tweetWithUser.get(i).user;
            tweets.add(tweet);
        }
        return tweets;
    }
}
