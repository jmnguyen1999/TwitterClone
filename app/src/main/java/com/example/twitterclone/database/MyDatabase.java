package com.example.twitterclone.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.twitterclone.models.Tweet;
import com.example.twitterclone.models.TweetDao;

/**
 * MyDatabase.java
 * Purpose:             Creates a database of Tweet objects in order to easily search for specific Tweets.
 *
 * Classes used:        Tweet{}, TweetDao{}
 *
 * @author Josephine Mai Nguyen
 * @version 1.0
 */
@Database(entities={Tweet.class}, version=2)
public abstract class MyDatabase extends RoomDatabase {
    public abstract TweetDao TweetDao();

    public static final String NAME = "MyDatabase";
}
