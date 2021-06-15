package com.codepath.apps.jotwitter.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TwitterDao {

    @Query("SELECT Tweet.body AS tweet_body, Tweet.createdAt AS tweet_createdAt, Tweet.id AS tweet_id, User.*" +
            " FROM Tweet INNER JOIN User ON Tweet.userID = user.id ORDER BY createdAt DESC LIMIT 3")
    List<TweetWithUser> recentItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets);              //method can take any number of tweets

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMode(User... users);
}
