package com.example.twitterclone.models;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * TweetDao.java
 * Purpose:         This is a Data Access Object (DAO) interface, which declares queries to return tweets based on certain filters and conditions. This is how we interact with our database "MyDatabase.java".
 *
 * Classes used:        Tweet{}, User{}
 *
 *  * @author Josephine Mai Nguyen
 *  * @version 1.0
 */
@Dao
public interface TweetDao {

    //Obtain 300 corresponding Tweets and their Users (TweetWithUser objects) along with all respective fields e.g. tweet body, timestamp, etc
    //Order all TweetWithUsers by their timestamp ("createdAt") in descending order
    @Query("SELECT Tweet.body AS tweet_body, Tweet.createdAt AS tweet_createdAt, Tweet.id AS tweet_id, User.*" +
            " FROM Tweet INNER JOIN User ON Tweet.userID = user.id ORDER BY createdAt DESC LIMIT 5")
    List<TweetWithUser> recentItems();

    //Insert any number of Tweets as an array into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTweet(Tweet... tweets);

    //Insert any number of Users as an array into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User... users);
}
