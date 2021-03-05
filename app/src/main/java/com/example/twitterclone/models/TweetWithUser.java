package com.example.twitterclone.models;

import androidx.room.Embedded;

import java.util.ArrayList;
import java.util.List;

/**
 * TweetWithUser.java
 * Purpose:                         Class used in correspondence with TweetDao.java, in order to return tuples of data with User and Tweet attributes.
 *
 * Classes used:                    User{}, Tweet{}
 *
 * @author Josephine Mai Nguyen
 * @version 2.0
 */
public class TweetWithUser {
    @Embedded       //puts the properties of User{} into this class
    User user;

    @Embedded(prefix = "tweet_")        //puts properties of Tweet{} into this class, prefixes all of Tweet{}'s attributes with "tweet_" to avoid ambiguity from User{}'s attribute names
    Tweet tweet;

    /**
     * Purpose:         Separates and returns the list of Tweets from a List of TweetWithUser
     * @param tweetWithUsers the List of TweetWithUser in question
     * @return tweets, the List of Tweet objects in the given tweetWithUsers
     */
    public static List<Tweet> getTweetList(List<TweetWithUser> tweetWithUsers){
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < tweetWithUsers.size(); i++){
            Tweet tweet = tweetWithUsers.get(i).tweet;
            tweet.user = tweetWithUsers.get(i).user;
            tweets.add(tweet);
        }
        return tweets;
    }
}
