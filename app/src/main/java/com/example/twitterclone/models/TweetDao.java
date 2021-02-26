package com.example.twitterclone.models;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * TweetDao.java
 * Purpose:         This is a Data Access Object (DAO) interface, which declares queries to return tweets based on certain filters and conditions. Queries implemented: Get one tweet via its id, Get list of tweets given timestamp ("createdAt")
 *
 * Classes used:        Tweet{}
 *
 *  * @author Josephine Mai Nguyen
 *  * @version 1.0
 */
@Dao
public interface TweetDao {

    //Get all tweets where Tweet.id = given id, name the method "byId()"
    @Query("SELECT * FROM Tweet WHERE id = :id")
    Tweet byId(long id);

    //Get all tweets where Tweet.createdAt = given createdAt, name the method "byCreatedAt()"
    @Query("SELECT * FROM Tweet WHERE createdAt = :createdAt")
    List<Tweet> byCreatedAt(String createdAt);

    //Replace strategy to update a table row:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTweet(Tweet... tweets);
}
